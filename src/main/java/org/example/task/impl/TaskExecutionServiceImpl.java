package org.example.task.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.exception.TaskExecutionException;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.example.service.TaskQueueService;
import org.example.task.BaseQueue;
import org.example.task.TaskExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private InstagramService instagramService;
    @Autowired
    private LoginService loginService;

    @Override
    @Transactional
    public void executeGetFollowerTask(TaskQueue task, LoginAccount loginAccount) {
        try {
            log.info("開始執行任務:{} ,帳號:{}", task.getTaskType(), loginAccount);
            loginAndUpdateAccountStatus(loginAccount);
            performTaskWithAccount(task);
            finalizeTask(task);
        } catch (TaskExecutionException e) {
            handleTaskFailure(task, loginAccount, e);
        }
    }

    //private

    /**
     * 使用帳號執行任務
     *
     * @param task 任務
     */
    private void performTaskWithAccount(TaskQueue task) {
        instagramService.searchTargetUserFollowersAndSave(task, task.getNextIdForSearch());
    }

    private void finalizeTask(TaskQueue task) {
        if (task.getNextIdForSearch() == null) {
            task.completeTask();
        } else {
            task.pauseTask();
        }
        taskQueueService.save(task);
        log.info("任务已保存:{}", task);
    }

    /**
     * 登入、檢查結果並更新登入帳號狀態
     */
    private void loginAndUpdateAccountStatus(LoginAccount loginAccount) {
        try {
            instagramService.login(loginAccount.getAccount(), loginAccount.getPassword());
        } catch (TaskExecutionException e) {
            handleLoginFailure(loginAccount, e);
        }
        loginAccount.loginAccountExhausted();
        loginService.save(loginAccount);
    }

    /**
     * 處理任務失敗
     */
    private void handleTaskFailure(TaskQueue task, LoginAccount loginAccount, TaskExecutionException e) {
        log.error("任務失敗，任務:{},帳號:{} ,更新任務狀態，並暫停掃描task_queue排程. 錯誤詳情: {}", task, loginAccount, e.getMessage());
        task.failTask(e.getMessage());
        stopBaseQueue();
        taskQueueService.save(task);
    }

    /**
     * 處理登入失敗
     */
    private void handleLoginFailure(LoginAccount loginAccount, TaskExecutionException e) {
        log.error("登入失敗，帳號:{} ,更新帳號狀態，錯誤詳情: {}", loginAccount, e.getMessage());
        loginAccount.loginAccountDeviant(e.getMessage());
        loginService.save(loginAccount);
    }
}
