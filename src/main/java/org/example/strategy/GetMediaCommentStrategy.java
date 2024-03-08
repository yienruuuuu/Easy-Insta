package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.Media;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueMedia;
import org.example.exception.SysCode;
import org.example.exception.TaskExecutionException;
import org.example.service.*;
import org.example.utils.CrawlingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/1
 */
@Slf4j
@Service("getMediaCommentStrategy")
public class GetMediaCommentStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final MediaService mediaService;
    private final MediaCommentService mediaCommentService;
    private final TaskQueueMediaService taskQueueMediaService;

    protected GetMediaCommentStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, MediaService mediaService, MediaCommentService mediaCommentService, TaskQueueMediaService taskQueueMediaService) {
        super(instagramService, loginService);
        this.taskQueueService = taskQueueService;
        this.mediaService = mediaService;
        this.mediaCommentService = mediaCommentService;
        this.taskQueueMediaService = taskQueueMediaService;
    }

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        log.info("開始執行任務:{} ,帳號:{}", taskQueue.getTaskConfig().getTaskType(), loginAccount);
        //登入、檢查結果並更新登入帳號狀態
        loginAndUpdateAccountStatus(loginAccount);
        //刪除舊的媒體留言資料
        deleteOldMediaContentData(taskQueue);
        //新增或更新taskQueue對於taskQueueMedia的指向
        getTaskQueueMediaAndSetInTaskQueue(taskQueue);
        //執行爬蟲任務
        performTaskWithAccount(taskQueue);
        //結束任務，依條件判斷更新任務狀態
        finalizeTask(taskQueue);
    }


    //private

    /**
     * 新增或更新taskQueue對於taskQueueMedia的指向
     *
     * @param taskQueue 任務
     */
    private void getTaskQueueMediaAndSetInTaskQueue(TaskQueue taskQueue) {
        TaskQueueMedia taskQueueMedia = taskQueueMediaService.findOneByTaskQueue(taskQueue, TaskStatusEnum.PAUSED)
                .orElseGet(() -> taskQueueMediaService.findOneByTaskQueue(taskQueue, TaskStatusEnum.PENDING)
                        .orElseThrow(() -> new TaskExecutionException(SysCode.TASK_QUEUE_MEDIA_NOT_FOUND))
                );
        taskQueue.updateTaskQueueMedia(taskQueueMedia);
        log.info("taskQueue 任務資料已更新:{} ", taskQueue);
    }

    /**
     * 刪除舊的媒體留言資料
     *
     * @param taskQueue 任務
     */
    private void deleteOldMediaContentData(TaskQueue taskQueue) {
        List<Integer> mediaIds = mediaService.listMediaByIgUserIdAndDateRange(taskQueue.getIgUser(), CrawlingUtil.getEarlyDateTime())
                .stream().map(Media::getId).toList();
        log.info("任務:{} ,貼文ID:{}", taskQueue, mediaIds);
        //初次進行時刪除舊的媒體留言資料
        if (TaskStatusEnum.PENDING.equals(taskQueue.getStatus())) {
            mediaCommentService.deleteOldMediaCotentDataByIgUserId(mediaIds);
        }
    }

    /**
     * 使用帳號執行任務
     *
     * @param task 任務
     */
    private void performTaskWithAccount(TaskQueue task) {
        instagramService.searchMediaCommentsAndSave(task, task.getNextIdForSearch());
    }

    /**
     * 結束任務判斷
     *
     * @param task 任務
     */
    private void finalizeTask(TaskQueue task) {
        updateTaskStatusBasedOnCondition(task);
        taskQueueService.save(task);
        log.info("任務已儲存:{}", task);
    }

    /**
     * 根據條件更新任務狀態
     *
     * @param task 任務
     */
    private void updateTaskStatusBasedOnCondition(TaskQueue task) {
        if (checkMedia(task)) {
            task.completeTask();
        } else if (task.getNextIdForSearch() != null) {
            task.pauseTask();
        } else {
            task.pendingTask();
        }
    }

    /**
     * 檢查爬取數量是否已達到結束標準 條件:最早貼文日期>當前日期-1年 || 爬取數量/實際貼文樹量>0.9
     *
     * @param task 任務
     * @return 是否已達到結束任務的標準
     */
    private boolean checkMedia(TaskQueue task) {
        int crawlerAmount = mediaService.countMediaByIgUser(task.getIgUser());
        int dbAmount = task.getIgUser().getMediaCount();
        log.info("任務:{} ,取得貼文數量:{},資料庫貼文數量:{}", task, dbAmount, crawlerAmount);

        // 計算當前日期-1年
        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(1);
        // 檢查是否存在最早的貼文日期大於當前日期-1年
        boolean existsEarlyMedia = mediaService.existsEarlyMediaBeforeCutoff(task.getIgUser(), cutoffDate);
        log.info("任務:{} ,是否已爬到最早的貼文日期，大於當前日期-1年 existsEarlyMedia:{}", task, existsEarlyMedia);
        return CrawlingUtil.isCrawlingCloseToRealFollowerCount(crawlerAmount, dbAmount, 0.9) || existsEarlyMedia;
    }
}
