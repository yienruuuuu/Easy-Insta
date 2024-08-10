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
@Service("getMediaCommentStrategy")
public class GetMediaCommentStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final MediaService mediaService;
    private final MediaCommentService mediaCommentService;

    protected GetMediaCommentStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, MediaService mediaService, MediaCommentService mediaCommentService, TaskQueueMediaService taskQueueMediaService) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
        this.mediaService = mediaService;
        this.mediaCommentService = mediaCommentService;
    }

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        //登入、檢查結果並更新登入帳號狀態
        loginByIgClientPool(loginAccount);
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
        instagramService.searchMediaCommentsAndSave(task, task.getTaskQueueMediaId().getNextMediaId());
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

    @Override
    protected void updateTaskStatusBasedOnCondition(TaskQueue task) {
        //若TaskQueueMedia.nextMediaId不為null，代表仍需繼續查詢，僅暫停任務
        if (task.getTaskQueueMediaId().getNextMediaId() != null) {
            task.pauseTask();
            return;
        }
        //若TaskQueueMedia.nextMediaId為null，代表該貼文已查詢完畢，更新子任務狀態為已完成
        task.getTaskQueueMediaId().setStatus(TaskStatusEnum.COMPLETED);
        //若TaskQueueMedia.nextMediaId為null，代表已查詢完畢，更新下一筆子任務指針，並暫停任務等待繼續
        getTaskQueueMediaWhichIsPausedOrPending(task).ifPresentOrElse(taskQueueMedia -> {
            task.updateTaskQueueMedia(taskQueueMedia);
            task.pauseTask();
        }, () -> {
            task.setTaskQueueMediaId(null);
            task.completeTask();
        });
    }
}
