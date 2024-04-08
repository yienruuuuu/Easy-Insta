package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.entity.TaskSendPromoteMessage;
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
@Service("sendPromotionMessageStrategy")
public class SendPromotionMessageStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final TaskSendPromoteMessageService taskSendPromoteMessageService;
    private final SeleniumHelperService seleniumHelperService;
    private final SeleniumService seleniumService;

    protected SendPromotionMessageStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, TaskQueueMediaService taskQueueMediaService, SeleniumService seleniumService, TaskSendPromoteMessageService taskSendPromoteMessageService, SeleniumHelperService seleniumHelperService) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
        this.taskSendPromoteMessageService = taskSendPromoteMessageService;
        this.seleniumHelperService = seleniumHelperService;
        this.seleniumService = seleniumService;
    }

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        WebDriver driver = seleniumService.getDriver();
        seleniumHelperService.waitForSeleniumReady(taskQueue, driver);
        //執行任務
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
        List<TaskSendPromoteMessage> promoteList = taskSendPromoteMessageService.findByTaskQueueAndStatus(task, TaskStatusEnum.PENDING);
        if (promoteList.isEmpty()) {
            throw new ApiException(SysCode.TASK_SEND_PROMOTE_MESSAGE_NOT_FOUNT);
        }
        promoteList.forEach(taskSendPromoteMessage -> {
            try {
                seleniumService.sendPromoteMessage(taskSendPromoteMessage, driver);
                taskSendPromoteMessage.setStatus(TaskStatusEnum.COMPLETED);
                log.info("粉絲帳號:{} ,執行完成", taskSendPromoteMessage.getAccount());
                CrawlingUtil.pauseBetweenRequests(3, 5);
            } catch (ApiException e) {
                log.error("任務:{} ,執行失敗", taskSendPromoteMessage, e);
                taskSendPromoteMessage.setStatus(TaskStatusEnum.FAILED);
            }
        });
        taskSendPromoteMessageService.saveAll(promoteList);
    }

    /**
     * 結束任務判斷
     *
     * @param task 任務
     */
    private void finalizeTask(TaskQueue task) {
        List<TaskSendPromoteMessage> promoteList = taskSendPromoteMessageService.findByTaskQueueAndStatus(task, TaskStatusEnum.PENDING);
        if (!promoteList.isEmpty()) {
            task.pauseTask();
        } else {
            task.completeTask();
        }
        taskQueueService.save(task);
        log.info("任務已儲存:{}", task);
    }


}
