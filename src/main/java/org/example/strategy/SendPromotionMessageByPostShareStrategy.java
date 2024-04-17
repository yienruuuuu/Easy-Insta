package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.ConfigEnum;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.config.ConfigCache;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.entity.TaskSendPromoteMessage;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.*;
import org.example.utils.CrawlingUtil;
import org.openqa.selenium.WebDriver;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 任務策略-發送推廣訊息
 *
 * @author Eric.Lee
 * Date: 2024/2/27
 */
@Slf4j
@Service("sendPromotionMessageByPostShareStrategy")
public class SendPromotionMessageByPostShareStrategy extends TaskStrategyBase implements TaskStrategy {
    private final TaskQueueService taskQueueService;
    private final TaskSendPromoteMessageService taskSendPromoteMessageService;
    private final SeleniumService seleniumService;
    private final ConfigCache configCache;

    protected SendPromotionMessageByPostShareStrategy(InstagramService instagramService, LoginService loginService, TaskQueueService taskQueueService, TaskQueueMediaService taskQueueMediaService, SeleniumService seleniumService, TaskSendPromoteMessageService taskSendPromoteMessageService, ConfigCache configCache) {
        super(instagramService, loginService, taskQueueMediaService);
        this.taskQueueService = taskQueueService;
        this.taskSendPromoteMessageService = taskSendPromoteMessageService;
        this.seleniumService = seleniumService;
        this.configCache = configCache;
    }

    @Override
    @Transactional
    public void executeTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        WebDriver driver = seleniumService.getDriver();

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
        int maxPromotionByPostSharePerDay = Integer.parseInt(configCache.get(ConfigEnum.MAX_PROMOTION_BY_POST_SHARE_PER_DAY.name()));
        List<TaskSendPromoteMessage> promoteList = taskSendPromoteMessageService.findByTaskQueueAndStatus(task, TaskStatusEnum.PENDING, PageRequest.of(0, maxPromotionByPostSharePerDay));
        if (promoteList.isEmpty()) {
            throw new ApiException(SysCode.TASK_SEND_PROMOTE_MESSAGE_BY_POST_SHARE_NOT_FOUNT);
        }
        //移動到準備畫面上
        String postUrl = seleniumService.readyForPromoteMessageByPostShare(promoteList.get(0), driver);

        promoteList.forEach(taskSendPromoteMessage -> {
            try {
                seleniumService.sendPromoteMessageByPostShare(taskSendPromoteMessage, driver);
                taskSendPromoteMessage.completeTask();
                log.info("帳號:{} ,執行完成", taskSendPromoteMessage.getAccount());
                CrawlingUtil.pauseBetweenRequests(3, 5);
            } catch (ApiException e) {
                log.error("帳號:{} ,執行失敗", taskSendPromoteMessage.getAccount(), e);
                taskSendPromoteMessage.failTask();
                //重新移動到準備畫面上
                driver.get(postUrl);
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
        List<TaskSendPromoteMessage> promoteList = taskSendPromoteMessageService.findByTaskQueueAndStatus(task, TaskStatusEnum.PENDING, Pageable.unpaged());
        if (!promoteList.isEmpty()) {
            task.pauseDailyTask();
        } else {
            task.completeDailyTask();
        }
        taskQueueService.save(task);
        log.info("任務已儲存:{}", task);
    }
}
