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

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/9
 */
@Slf4j
@Service("getMediaLikerStrategy")
public class GetMediaLikerStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final MediaService mediaService;
    private final MediaLikerService mediaLikerService;

    protected GetMediaLikerStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, MediaService mediaService, MediaLikerService mediaLikerService, TaskQueueMediaService taskQueueMediaService) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
        this.mediaService = mediaService;
        this.mediaLikerService = mediaLikerService;
    }

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
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
        TaskQueueMedia taskQueueMedia = getTaskQueueMediaWhichIsPausedOrPending(taskQueue)
                .orElseThrow(() -> new TaskExecutionException(SysCode.TASK_QUEUE_MEDIA_NOT_FOUND));
        taskQueue.updateTaskQueueMedia(taskQueueMedia);
        log.info("taskQueue 任務資料已更新:{} ", taskQueue);
    }

    /**
     * 刪除舊的媒體按讚資料
     *
     * @param taskQueue 任務
     */
    private void deleteOldMediaContentData(TaskQueue taskQueue) {
        if (!TaskStatusEnum.PENDING.equals(taskQueue.getStatus())) return;

        //初次進行時刪除舊的媒體留言資料
        List<Integer> mediaIds = mediaService.listMediaByIgUserIdAndDateRange(taskQueue.getIgUser(), CrawlingUtil.getEarlyDateTime())
                .stream().map(Media::getId).toList();
        log.info("任務:{} ,貼文ID:{}", taskQueue, mediaIds);
        mediaLikerService.deleteOldMediaLikerByIgUserId(mediaIds);
    }

    /**
     * 使用帳號執行任務
     *
     * @param task 任務
     */
    private void performTaskWithAccount(TaskQueue task) {
        instagramService.searchMediaLikersAndSave(task, task.getNextIdForSearch());
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

}
