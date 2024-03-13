package org.example.task.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.exception.TaskExecutionException;
import org.example.service.LoginService;
import org.example.service.TaskQueueService;
import org.example.strategy.TaskExecutionStrategyFactory;
import org.example.strategy.TaskStrategy;
import org.example.task.BaseQueue;
import org.example.task.TaskExecutionService;
import org.springframework.stereotype.Service;

/**
 * @author Eric.Lee
 * Date:2024/2/19
 */
@Slf4j
@Service("taskExecutionService")
public class TaskExecutionServiceImpl extends BaseQueue implements TaskExecutionService {

    private final TaskQueueService taskQueueService;
    private final LoginService loginService;
    // 注入策略工廠
    private final TaskExecutionStrategyFactory strategyFactory;

    public TaskExecutionServiceImpl(TaskQueueService taskQueueService, TaskExecutionStrategyFactory strategyFactory, LoginService loginService) {
        this.taskQueueService = taskQueueService;
        this.strategyFactory = strategyFactory;
        this.loginService = loginService;
    }

    public void executeTask(TaskQueue task, LoginAccount loginAccount) {
        TaskStrategy strategy = getStrategy(task);
        try {
            strategy.executeTask(task, loginAccount);

        } catch (ApiException apiException) {
            handleApiException(apiException, task, loginAccount);
        } catch (TaskExecutionException e) {
            handleTaskFailure(task, loginAccount, e);
        }
    }


    // private

    /**
     * 處理ApiException
     */
    private void handleApiException(ApiException apiException, TaskQueue task, LoginAccount loginAccount) {
        if (apiException.getCode() == SysCode.IG_ACCOUNT_CHALLENGE_REQUIRED) {
            handleChallengeRequired(task, loginAccount, apiException);
        } else if (apiException.getCode() == SysCode.SOCKET_TIMEOUT) {
            handleSocketTimeOut(task, loginAccount, apiException);
        } else if (apiException.getCode() == SysCode.TASK_QUEUE_FOLLOWER_DETAIL_NOT_FOUNT) {
            task.completeTask();
            taskQueueService.save(task);
        } else {
            throw apiException;
        }
    }

    /**
     * 處理連線失敗
     */
    private void handleSocketTimeOut(TaskQueue task, LoginAccount loginAccount, ApiException e) {
        log.error("任務失敗，任務:{},帳號:{} ,更新帳號為EXHAUSTED 錯誤詳情: {}", task, loginAccount, e.getMessage(), e);
        //更新任務狀態
        task.pauseTask();
        log.info("任務暫停，任務:{}", task);
        taskQueueService.save(task);
        //更新登入帳號狀態
        loginAccount.loginAccountExhausted();
        loginService.save(loginAccount);
    }

    /**
     * 處理帳號失敗
     */
    private void handleChallengeRequired(TaskQueue task, LoginAccount loginAccount, ApiException e) {
        log.error("任務失敗，任務:{},帳號:{} ,更新帳號為DEVIANT 錯誤詳情: {}", task, loginAccount, e.getMessage(), e);
        //更新任務狀態
        task.pauseTask();
        task.getTaskQueueMediaId().setStatus(TaskStatusEnum.PAUSED);
        log.info("任務暫停，任務:{}", task);
        taskQueueService.save(task);
        //更新登入帳號狀態
        loginAccount.loginAccountDeviant(e.getMessage());
        loginService.save(loginAccount);
    }

    /**
     * 處理任務失敗
     */
    private void handleTaskFailure(TaskQueue task, LoginAccount loginAccount, TaskExecutionException e) {
        log.error("任務失敗，任務:{},帳號:{} ,更新任務狀態，並暫停掃描task_queue排程. 錯誤詳情: {}", task, loginAccount, e.getMessage(), e);
        //停止任務檢核排程
        stopBaseQueue();
        //更新任務狀態
        task.failTask(e.getMessage());
        taskQueueService.save(task);
        //更新登入帳號狀態
        loginAccount.loginAccountDeviant(e.getMessage());
        loginService.save(loginAccount);

    }

    /**
     * 取得策略，如果找不到對應的策略類型，則更新任務狀態
     */
    private TaskStrategy getStrategy(TaskQueue task) {
        TaskStrategy strategy = strategyFactory.getStrategy(task.getTaskConfig().getTaskType());

        if (strategy == null) {
            task.failTask(SysCode.TASK_TYPE_NOT_FOUND_IN_STRATEGY_FACTORY.getMessage());
            taskQueueService.save(task);
            throw new ApiException(SysCode.TASK_TYPE_NOT_FOUND_IN_STRATEGY_FACTORY);
        }

        return strategy;
    }
}
