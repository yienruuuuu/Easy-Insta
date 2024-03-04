package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.dto.ApiResponse;
import org.example.bean.dto.CalculateMediaParams;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.IgUser;
import org.example.entity.LoginAccount;
import org.example.entity.Media;
import org.example.entity.TaskQueue;
import org.example.exception.SysCode;
import org.example.service.*;
import org.example.utils.CrawlingUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 */
@Slf4j
@Tag(name = "USER controller", description = "IG 用戶相關API")
@RestController
@RequestMapping("iguser")
public class IgUserController extends BaseController {

    private final LoginService loginService;
    private final InstagramService instagramService;
    private final IgUserService igUserService;
    private final TaskQueueService taskQueueService;
    private final MediaService mediaService;

    public IgUserController(LoginService loginService, InstagramService instagramService, IgUserService igUserService, TaskQueueService taskQueueService, MediaService mediaService) {
        this.loginService = loginService;
        this.instagramService = instagramService;
        this.igUserService = igUserService;
        this.taskQueueService = taskQueueService;
        this.mediaService = mediaService;
    }

    @Operation(summary = "以用戶名查詢用戶，並可控是否紀錄到資料庫")
    @PostMapping(value = "/search/{username}/{needToWriteToDb}")
    public IgUser getUserInfoByUserName(@PathVariable String username, @PathVariable boolean needToWriteToDb) {
        LoginAccount loginAccount = loginService.getLoginAccount();
        IgUser igUser = instagramService.searchUser(username, loginAccount);
        // 檢查是否需要寫入資料庫,保存或更新使用者訊息
        if (needToWriteToDb) {
            igUserService.save(igUser);
            log.info("用户信息已保存或更新 : {}", igUser.getUserName());
        } else {
            log.info("不需要寫入資料庫 使用者名稱: {}", igUser.getUserName());
        }
        return igUser;
    }

    @Operation(summary = "提交排程，安排任務")
    @PostMapping(value = "/task/{taskEnum}/{username}")
    public Object sendTask(@PathVariable String username, @PathVariable TaskTypeEnum taskEnum) {
        IgUser targetUser = igUserService.findUserByIgUserName(username);
        log.info("確認任務對象，用戶: {}存在", targetUser.getUserName());
        // 檢查對於查詢對象的任務是否存在
        boolean taskExists = taskQueueService.checkGetFollowersTaskQueueExist(targetUser, taskEnum);
        if (taskExists) {
            log.info("用戶: {} 的 {} 任務已存在", taskEnum, username);
            return new ApiResponse(SysCode.TASK_ALREADY_EXISTS.getCode(), SysCode.TASK_ALREADY_EXISTS.getMessage(), null);
        }
        // 保存任务並返回保存的任务
        Optional<TaskQueue> savedTask = taskQueueService.createAndSaveTaskQueue(targetUser, taskEnum, TaskStatusEnum.PENDING);
        if (savedTask.isPresent()) {
            log.info("username: {} 的 {} 任務創建成功", taskEnum, username);
            return savedTask.get();
        } else {
            log.info("username: {}的 {} 任務建立失敗", taskEnum, username);
            return new ApiResponse(SysCode.TASK_CREATION_FAILED.getCode(), SysCode.TASK_CREATION_FAILED.getMessage(), null);
        }
    }

    @Operation(summary = "計算互動率", description = "請先確定都取得了當下的最新資料")
    @PostMapping(value = "/interactionRate/{userName}")
    public double calculateInteractionRate(@PathVariable String userName,
                                           @Parameter(description = "日期格式为 YYYY-MM-DD",
                                                  example = "2024-03-04",
                                                  schema = @Schema(type = "string", format = "date"))
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        IgUser targetUser = igUserService.findUserByIgUserName(userName);
        log.info("確認對象，用戶: {}存在, date={}", targetUser.getUserName(), date.atStartOfDay());
        //取出media
        List<Media> mediaList = mediaService.listMediaByIgUserIdAndDateRange(targetUser, date.atStartOfDay());
        CalculateMediaParams params = getCalculateMediaParams(targetUser, mediaList);
        log.info("計算互動率，用戶: {} ，計算參數:{}", targetUser.getUserName(), params);
        return CrawlingUtil.calculateEngagementRate(params.getLikes(), params.getComments(), params.getShares(), params.getFollowers(), params.getPostAmounts());
    }


    // private

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
}