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
import org.openqa.selenium.WebDriver;
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
    private final TaskQueueFollowerDetailService taskQueueFollowerDetailService;
    private final SeleniumHelperService seleniumHelperService;
    private final SeleniumService seleniumService;


    protected GetFollowerDetailStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, TaskQueueMediaService taskQueueMediaService, SeleniumService seleniumService, TaskQueueFollowerDetailService taskQueueFollowerDetailService, SeleniumHelperService seleniumHelperService) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
        this.taskQueueFollowerDetailService = taskQueueFollowerDetailService;
        this.seleniumHelperService = seleniumHelperService;
        this.seleniumService = seleniumService;
    }

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        WebDriver driver = seleniumService.getDriver();
        seleniumHelperService.waitForSeleniumReady(taskQueue, driver);
        //執行爬蟲任務
        performTask(taskQueue, driver);
        //結束任務，依條件判斷更新任務狀態
        finalizeTask(taskQueue);
    }


    //private


    /**
     * 執行任務
     *
     * @param task 任務
     */
    private void performTask(TaskQueue task, WebDriver driver) {
        List<TaskQueueFollowersDetail> taskQueuePage =
                taskQueueFollowerDetailService.findByTaskQueueAndStatusByPage(TaskStatusEnum.PENDING, task, 0, 30).getContent();
        if (taskQueuePage.isEmpty()) {
            throw new ApiException(SysCode.TASK_QUEUE_FOLLOWER_DETAIL_NOT_FOUNT);
        }
        taskQueuePage.forEach(taskQueueFollowersDetail -> {
            try {
                seleniumService.crawlFollowerDetailByCssStyle(taskQueueFollowersDetail.getFollower(), driver);
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
