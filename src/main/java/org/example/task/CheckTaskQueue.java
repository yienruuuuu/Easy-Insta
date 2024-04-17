package org.example.task;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.LoginService;
import org.example.service.TaskQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
@Slf4j
@Service("checkTaskQueue")
public class CheckTaskQueue extends BaseQueue {
    private final TaskQueueService taskQueueService;
    private final LoginService loginService;
    private final TaskExecutionService taskExecutionService;

    public CheckTaskQueue(TaskQueueService taskQueueService, LoginService loginService, TaskExecutionService taskExecutionService) {
        this.taskQueueService = taskQueueService;
        this.loginService = loginService;
        this.taskExecutionService = taskExecutionService;
    }

    /**
     * 每日早上8點將所有DAILY_PAUSED的任務狀態修改為DAILY_PENDING
     */
    @Scheduled(cron = "0 0 8 * * ?", zone = "Asia/Taipei")
    public void modifyDailyTaskStatus() {
        List<TaskQueue> tasks = taskQueueService.findTasksByStatus(TaskStatusEnum.DAILY_PAUSED);
        tasks.forEach(task -> task.setStatus(TaskStatusEnum.DAILY_PENDING));
        taskQueueService.saveAll(tasks);
    }

    @Scheduled(fixedDelayString = "${taskQueue.checkDelay:10000}")
    public void checkTasks() {
        log.info("開始檢查任務佇列");
        if (!checkTaskEnabled()) return;

        // 非同步執行需要登入的任務檢查
        executeAsync(this::checkAndExecuteNeedLoginTasks);
        // 非同步執行不需要登入的任務檢查
        executeAsync(this::checkAndExecuteWithoutLoginTasks);

        log.info("任務佇列檢查結束");
    }


    //private

    /**
     * 非同步執行任務
     *
     * @param task 任務
     */
    private void executeAsync(Runnable task) {
        CompletableFuture.runAsync(task)
                .exceptionally(ex -> {
                    log.error("非同步任務執行出錯", ex);
                    return null;
                });
    }

    private void checkAndExecuteNeedLoginTasks() {
        checkAndExecuteTasks(true);
    }

    private void checkAndExecuteWithoutLoginTasks() {
        checkAndExecuteTasks(false);
    }

    /**
     * 檢查並執行任務
     *
     * @param needLogin 是否需要登入
     */
    private void checkAndExecuteTasks(boolean needLogin) {
        if (needLogin && isInProgressTaskExists(needLogin)) return;

        try {
            LoginAccount loginAccount = needLogin ? loginService.getLoginAccount() : null;
            TaskQueue task = getTask(Arrays.asList(TaskStatusEnum.PAUSED, TaskStatusEnum.PENDING), needLogin);
            updateAndExecuteTask(task, loginAccount);
        } catch (ApiException e) {
            log.info("任務序列發生預期事件 {}", e.getMessage());
        } catch (Exception e) {
            log.error("任務序列發生特殊錯誤事件, 暫停任務排程, 請手動處理錯誤", e);
            stopBaseQueue();
        }
    }

    /**
     * 檢查是否有正在執行中且需要登入的任務
     *
     * @return 是否有正在執行中的任務
     */
    private boolean isInProgressTaskExists(boolean needLogin) {
        List<TaskStatusEnum> statuses = List.of(TaskStatusEnum.IN_PROGRESS);
        boolean exists = taskQueueService.checkTasksByStatusAndNeedLogin(statuses, needLogin);
        if (exists) {
            log.info("有需要登入(NEED LOGIN IG)的任務正在執行中(IN_PROGRESS)，不執行新任務");
        }
        return exists;
    }

    /**
     * 根據任務狀態列表statusList查詢須執行的任務是否存在
     *
     * @param statusList 任務狀態列表
     */
    private TaskQueue getTask(List<TaskStatusEnum> statusList, boolean needLogin) {
        return statusList.stream()
                .map(status -> taskQueueService.findFirstTaskQueueByStatusAndNeedLogin(status, needLogin))
                .flatMap(Optional::stream)
                .findFirst()
                .orElseThrow(() -> new ApiException(SysCode.NO_TASKS_TO_PERFORM));
    }

    /**
     * 先更新任務狀態，再執行任務
     *
     * @param taskQueue 任務
     */
    private void updateAndExecuteTask(TaskQueue taskQueue, LoginAccount loginAccount) {
        // 更新任務狀態為IN_PROGRESS
        TaskQueue latestTaskQueue = taskQueueService.updateTaskStatus(taskQueue.getId(), TaskStatusEnum.IN_PROGRESS);
        // 執行任務
        taskExecutionService.executeTask(latestTaskQueue, loginAccount);
    }

}
