package org.example.task.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.exception.TaskExecutionException;
import org.example.service.FollowersService;
import org.example.service.IgUserService;
import org.example.service.TaskQueueService;
import org.example.strategy.TaskExecutionStrategyFactory;
import org.example.strategy.TaskStrategy;
import org.example.task.BaseQueue;
import org.example.task.TaskExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Eric.Lee
 * Date:2024/2/19
 */
@Slf4j
@Service("taskExecutionService")
public class TaskExecutionServiceImpl extends BaseQueue implements TaskExecutionService {
    @Autowired
    private TaskQueueService taskQueueService;
    @Autowired
    FollowersService followersService;
    @Autowired
    IgUserService igUserService;
    @Autowired
    private TaskExecutionStrategyFactory strategyFactory; // 注入策略工廠

    public void executeTask(TaskQueue task, LoginAccount loginAccount) {
        TaskStrategy strategy = strategyFactory.getStrategy(task.getTaskConfig().getTaskType());
        if (strategy != null) {
            try {
                strategy.executeTask(task, loginAccount);
            } catch (TaskExecutionException e) {
                handleTaskFailure(task, loginAccount, e);
            }
        } else {
            log.error("找不到對應的策略類型: {}", task.getTaskConfig().getTaskType());
        }
    }

    /**
     * 處理任務失敗
     */
    private void handleTaskFailure(TaskQueue task, LoginAccount loginAccount, TaskExecutionException e) {
        log.error("任務失敗，任務:{},帳號:{} ,更新任務狀態，並暫停掃描task_queue排程. 錯誤詳情: {}", task, loginAccount, e.getMessage(), e);
        task.failTask(e.getMessage());
        stopBaseQueue();
        taskQueueService.save(task);
    }


}
