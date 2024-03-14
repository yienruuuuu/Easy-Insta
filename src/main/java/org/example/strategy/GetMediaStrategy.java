package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.service.*;
import org.example.utils.CrawlingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author Eric.Lee
 * Date: 2024/3/1
 */
@Slf4j
@Service("getMediaStrategy")
public class GetMediaStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final MediaService mediaService;

    protected GetMediaStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, MediaService mediaService, TaskQueueMediaService taskQueueMediaService) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
        this.mediaService = mediaService;
    }

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        //登入、檢查結果並更新登入帳號狀態
        loginAndUpdateAccountStatus(loginAccount);
        //執行爬蟲任務
        performTaskWithAccount(taskQueue);
        //結束任務，依條件判斷更新任務狀態
        finalizeTask(taskQueue);
    }


    //private

    /**
     * 使用帳號執行任務
     *
     * @param task 任務
     */
    private void performTaskWithAccount(TaskQueue task) {
        instagramService.searchUserMediasAndSave(task, task.getNextIdForSearch());
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
    @Override
    protected void updateTaskStatusBasedOnCondition(TaskQueue task) {
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
        boolean isCrawlingCloseToRealFollowerCount = CrawlingUtil.isCrawlingCloseToRealFollowerCount(crawlerAmount, dbAmount, 1.0);
        log.info("任務:{} ,是否已爬到最早的貼文日期，大於當前日期-1年 existsEarlyMedia:{}", task, existsEarlyMedia);
        log.info("是否已達到設定貼文比例 = {}", isCrawlingCloseToRealFollowerCount);
        return isCrawlingCloseToRealFollowerCount || existsEarlyMedia;
    }

    public static void main(String[] args) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusYears(1);
        System.out.println(cutoffDate);
    }
}
