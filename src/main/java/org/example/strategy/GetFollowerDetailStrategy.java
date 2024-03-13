package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueFollowersDetail;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.*;
import org.example.utils.CrawlingUtil;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Eric.Lee
 * Date: 2024/2/27
 */
@Slf4j
@Service("getFollowerDetailStrategy")
public class GetFollowerDetailStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final FollowersService followersService;
    private final SeleniumService seleniumService;
    private final TaskQueueFollowerDetailService taskQueueFollowerDetailService;

    protected GetFollowerDetailStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, FollowersService followersService, TaskQueueMediaService taskQueueMediaService, SeleniumService seleniumService, TaskQueueFollowerDetailService taskQueueFollowerDetailService) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
        this.followersService = followersService;
        this.seleniumService = seleniumService;
        this.taskQueueFollowerDetailService = taskQueueFollowerDetailService;
    }

    //等待Selenium準備完成的上限時間
    private static final long TIME_OUT = 60000;

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        waitForSeleniumReady();
        //執行爬蟲任務
        performTask(taskQueue);
//        //結束任務，依條件判斷更新任務狀態
//        finalizeTask(taskQueue);
    }


    //private

    /**
     * 等待Selenium準備完成
     */
    private void waitForSeleniumReady() {
        long startTime = System.currentTimeMillis();
        while (!seleniumService.isReadyForCrawl()) {
            log.info("等待Selenium準備完成");
            if (System.currentTimeMillis() - startTime > TIME_OUT) {
                throw new ApiException(SysCode.TASK_WAIT_SELENIUM_TIME_OUT);
            }
            CrawlingUtil.pauseBetweenRequests(10, 10);
        }
    }

    /**
     * 執行任務
     *
     * @param task 任務
     */
    private void performTask(TaskQueue task) {
        Page<TaskQueueFollowersDetail> taskQueuePage = taskQueueFollowerDetailService.findByTaskQueueAndStatusByPage(TaskStatusEnum.PENDING, task, 0, 30);
        if (taskQueuePage.isEmpty()) {
            log.info("任務:{} 無任務明細", task);
            return;
        }
        log.info("任務:{} 任務明細:{}", task, taskQueuePage.getContent());
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
        if (task.getNextIdForSearch() == null && checkFollowerAmount(task)) {
            task.completeTask();
        } else if (task.getNextIdForSearch() != null) {
            task.pauseTask();
        } else {
            task.pendingTask();
        }
    }

    /**
     * 檢查爬取數量是否已達到結束排成標準
     *
     * @param task 任務
     * @return 是否已達到結束任務的標準
     */
    private boolean checkFollowerAmount(TaskQueue task) {
        int crawlerAmount = followersService.countFollowersByIgUserName(task.getIgUser());
        int dbAmount = task.getIgUser().getFollowerCount();
        log.info("任務:{} ,取追蹤者數量:{},資料庫追蹤者數量:{}", task, dbAmount, crawlerAmount);
        return CrawlingUtil.isCrawlingCloseToRealFollowerCount(crawlerAmount, dbAmount, 0.9);
    }
}
