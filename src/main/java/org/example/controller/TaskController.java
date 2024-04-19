package org.example.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.dto.CalculateMediaParams;
import org.example.bean.dto.PromotionRequest;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.*;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.*;
import org.example.utils.CrawlingUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Eric.Lee
 */
@Slf4j
@Tag(name = "MAIN controller", description = "主要功能 API")
@RestController
@RequestMapping("main")
public class TaskController {

    private final LoginService loginService;
    private final InstagramService instagramService;
    private final IgUserService igUserService;
    private final TaskQueueService taskQueueService;
    private final MediaService mediaService;
    private final TaskSendPromoteMessageService taskSendPromoteMessageService;

    public TaskController(LoginService loginService, InstagramService instagramService, IgUserService igUserService, TaskQueueService taskQueueService, MediaService mediaService, TaskSendPromoteMessageService taskSendPromoteMessageService) {
        this.loginService = loginService;
        this.instagramService = instagramService;
        this.igUserService = igUserService;
        this.taskQueueService = taskQueueService;
        this.mediaService = mediaService;
        this.taskSendPromoteMessageService = taskSendPromoteMessageService;
    }

    @Operation(summary = "以用戶名查詢用戶，並可控是否紀錄到資料庫")
    @PostMapping(value = "/search/{username}/{needToWriteToDb}")
    public IgUser getUserInfoByUserName(@PathVariable String username, @PathVariable boolean needToWriteToDb) {
        LoginAccount loginAccount = loginService.getLoginAccount();
        IgUser igUser = instagramService.searchUser(username, loginAccount);
        // 檢查是否需要寫入資料庫,保存或更新使用者訊息
        if (!needToWriteToDb) {
            return igUser;
        }
        // 保存或更新使用者訊息
        igUserService.saveOrUpdateIgUser(igUser);
        loginAccount.loginAccountExhausted();
        loginService.save(loginAccount);
        return igUser;
    }

    @Operation(summary = "提交排程，安排任務")
    @PostMapping(value = "/task/{taskEnum}/{userName}", consumes = "multipart/form-data")
    @Transactional
    public TaskQueue sendTask(@PathVariable String userName, @PathVariable TaskTypeEnum taskEnum, @RequestParam(value = "file", required = false) MultipartFile file) {
        //確認用戶存在
        IgUser targetUser = getUser(userName);
        //準備任務佇列
        TaskQueue taskQueue = prepareTaskQueue(targetUser, taskEnum);
        //處理任務細項
        return processTask(taskQueue, file);
    }


    @Operation(summary = "計算互動率", description = "請先確定都取得了當下的最新資料")
    @PostMapping(value = "/interactionRate/{userName}")
    public double calculateInteractionRate(@PathVariable String userName,
                                           @Parameter(description = "日期格式为 YYYY-MM-DD",
                                                   example = "2024-03-04",
                                                   schema = @Schema(type = "string", format = "date"))
                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        IgUser targetUser = igUserService.findUserByIgUserName(userName).orElseThrow(() -> new ApiException(SysCode.IG_USER_NOT_FOUND_IN_DB));
        log.info("確認對象，用戶: {}存在, date={}", targetUser.getUserName(), date.atStartOfDay());
        //取出media
        List<Media> mediaList = mediaService.listMediaByIgUserIdAndDateRange(targetUser, date.atStartOfDay());
        CalculateMediaParams params = getCalculateMediaParams(targetUser, mediaList);
        log.info("計算互動率，用戶: {} ，計算參數:{}", targetUser.getUserName(), params);
        return CrawlingUtil.calculateEngagementRate(params.getLikes(), params.getComments(), params.getShares(), params.getFollowers(), params.getPostAmounts());
    }


    // private

    /**
     * 取得計算互動率的參數
     *
     * @param targetUser 用戶
     * @param mediaList  用戶的media列表
     * @return CalculateMediaParams
     */
    private CalculateMediaParams getCalculateMediaParams(IgUser targetUser, List<Media> mediaList) {
        int totalLikes = 0;
        int totalComments = 0;
        int totalReshares = 0;
        // 遍歷mediaList來累積likes, comments和reshares
        for (Media media : mediaList) {
            totalLikes += media.getLikeCount() != null ? media.getLikeCount() : 0;
            totalComments += media.getCommentCount() != null ? media.getCommentCount() : 0;
            totalReshares += media.getReshareCount() != null ? media.getReshareCount() : 0;
        }
        return CalculateMediaParams.builder()
                .likes(totalLikes)
                .comments(totalComments)
                .shares(totalReshares)
                .followers(targetUser.getFollowerCount())
                .postAmounts(targetUser.getMediaCount())
                .build();
    }

    /**
     * 取得用戶
     *
     * @param userName 用戶名
     * @return IgUser
     */
    private IgUser getUser(String userName) {
        return igUserService.findUserByIgUserName(userName)
                .orElseThrow(() -> new ApiException(SysCode.IG_USER_NOT_FOUND_IN_DB));
    }

    /**
     * 準備任務佇列
     *
     * @param user     用戶
     * @param taskType 任務類型
     * @return TaskQueue
     */
    private TaskQueue prepareTaskQueue(IgUser user, TaskTypeEnum taskType) {
        if (taskQueueService.checkTaskQueueExistByUserAndTaskType(user, taskType)) {
            throw new ApiException(SysCode.TASK_ALREADY_EXISTS);
        }
        return taskQueueService.createTaskQueueAndDeleteOldData(user, taskType);
    }

    /**
     * 處理任務
     *
     * @param taskQueue 任務
     * @param file      檔案
     * @return TaskQueue
     */
    private TaskQueue processTask(TaskQueue taskQueue, MultipartFile file) {
        //是否為推廣訊息任務
        if (isPromotionMessageTask(taskQueue.getTaskConfig().getTaskType())) {
            return handlePromotionMessageTask(taskQueue, file);
        }
        return taskQueue;
    }

    /**
     * 判斷是否為推廣訊息任務
     *
     * @param taskType 任務類型
     */
    private boolean isPromotionMessageTask(TaskTypeEnum taskType) {
        return TaskTypeEnum.SEND_PROMOTE_MESSAGE.equals(taskType) ||
                TaskTypeEnum.SEND_PROMOTE_MESSAGE_BY_POST_SHARE.equals(taskType);
    }

    /**
     * 處理推廣訊息任務
     *
     * @param taskQueue 任務
     * @param file      檔案
     * @return TaskQueue
     */
    private TaskQueue handlePromotionMessageTask(TaskQueue taskQueue, MultipartFile file) {
        try {
            checkAndSaveTaskPromotionDetail(upload(file), taskQueue);
        } catch (Exception e) {
            log.error("文件上傳失敗", e);
            throw new ApiException(SysCode.FILE_UPLOAD_FAILED);
        }
        return taskQueue;
    }

    /**
     * 讀取推廣清單Excel
     *
     * @return 帳密清單
     */
    private List<PromotionRequest> upload(MultipartFile file) throws IOException {
        if (file == null) {
            throw new ApiException(SysCode.FILE_NOT_FOUND);
        }
        List<PromotionRequest> dataList = Lists.newArrayList();
        EasyExcelFactory.read(file.getInputStream(), PromotionRequest.class, new ReadListener<PromotionRequest>() {
            @Override
            public void invoke(PromotionRequest data, AnalysisContext context) {
                dataList.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // do nothing
            }
        }).sheet("私訊列表").doRead();
        return dataList;
    }

    /**
     * 檢查並保存任務推廣明細
     *
     * @param promotionRequestList 推廣請求列表
     * @param taskQueue            任務
     */
    private void checkAndSaveTaskPromotionDetail(List<PromotionRequest> promotionRequestList, TaskQueue taskQueue) {
        List<TaskSendPromoteMessage> taskSendPromoteMessageList = Lists.newArrayList();
        for (PromotionRequest promotionRequest : promotionRequestList) {
            TaskSendPromoteMessage taskSendPromoteMessage = TaskSendPromoteMessage.builder()
                    .taskQueue(taskQueue)
                    .account(promotionRequest.getAccount())
                    .accountFullName(promotionRequest.getAccountFullName())
                    .textEn(promotionRequest.getTextEn())
                    .textZhTw(promotionRequest.getTextZhTw())
                    .textJa(promotionRequest.getTextJa())
                    .textRu(promotionRequest.getTextRu())
                    .postUrl(promotionRequest.getPostUrl())
                    .status(TaskStatusEnum.PENDING)
                    .createTime(taskQueue.getSubmitTime())
                    .build();
            taskSendPromoteMessageList.add(taskSendPromoteMessage);
        }
        taskSendPromoteMessageService.saveAll(taskSendPromoteMessageList);
    }
}