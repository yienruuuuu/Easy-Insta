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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/2/27
 */
@Slf4j
@Service("getFollowerDetailStrategy")
public class GetFollowerDetailStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final SeleniumService seleniumService;
    private final TaskQueueFollowerDetailService taskQueueFollowerDetailService;

    protected GetFollowerDetailStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, TaskQueueMediaService taskQueueMediaService, SeleniumService seleniumService, TaskQueueFollowerDetailService taskQueueFollowerDetailService) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
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
        //結束任務，依條件判斷更新任務狀態
        finalizeTask(taskQueue);
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
        List<TaskQueueFollowersDetail> taskQueuePage =
                taskQueueFollowerDetailService.findByTaskQueueAndStatusByPage(TaskStatusEnum.PENDING, task, 0, 30).getContent();
        if (taskQueuePage.isEmpty()) {
            throw new ApiException(SysCode.TASK_QUEUE_FOLLOWER_DETAIL_NOT_FOUNT);
        }
        taskQueuePage.forEach(taskQueueFollowersDetail -> {
            try {
                seleniumService.crawlFollowerDetailByCssStyle(taskQueueFollowersDetail.getFollower());
                taskQueueFollowersDetail.setStatus(TaskStatusEnum.COMPLETED);
                log.info("粉絲明細:{} ,執行完成", taskQueueFollowersDetail);
                CrawlingUtil.pauseBetweenRequests(3, 5);
            } catch (ApiException e) {
                log.error("任務:{} ,執行失敗", taskQueueFollowersDetail, e);
                taskQueueFollowersDetail.setStatus(TaskStatusEnum.FAILED);
            }
        });
        taskQueueFollowerDetailService.saveAll(taskQueuePage);
    }

    /**
     * 結束任務判斷
     *
     * @param task 任務
     */
    private void finalizeTask(TaskQueue task) {
        List<TaskQueueFollowersDetail> taskQueuePage =
                taskQueueFollowerDetailService.findByTaskQueueAndStatusByPage(TaskStatusEnum.PENDING, task, 0, 1).getContent();
        if (!taskQueuePage.isEmpty()) {
            task.pauseTask();
        } else {
            task.completeTask();
        }
        taskQueueService.save(task);
        log.info("任務已儲存:{}", task);
    }

}
