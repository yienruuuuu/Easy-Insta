package org.example.task.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.exception.TaskExecutionException;
import org.example.service.*;
import org.example.task.BaseQueue;
import org.example.task.TaskExecutionService;
import org.example.utils.FollowerCrawlingUtil;
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
    @Autowired
    FollowersService followersService;
    @Autowired
    IgUserService igUserService;

    @Override
    @Transactional
    public void executeGetFollowerTask(TaskQueue task, LoginAccount loginAccount) {
        try {
            log.info("開始執行任務:{} ,帳號:{}", task.getTaskType(), loginAccount);
            //登入、檢查結果並更新登入帳號狀態
            loginAndUpdateAccountStatus(loginAccount);
            //執行爬蟲任務
            performTaskWithAccount(task);
            //結束任務，依條件判斷更新任務狀態
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

    /**
     * 結束任務判斷
     *
     * @param task 任務
     */
    private void finalizeTask(TaskQueue task) {
        updateTaskStatusBasedOnCondition(task);
        taskQueueService.save(task);
        log.info("任务已保存:{}", task);
    }

    /**
     * 根據條件更新任務狀態
     *
     * @param task 任務
     */
    private void updateTaskStatusBasedOnCondition(TaskQueue task) {
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
        int crawlerAmount = followersService.countFollowersByIgUserName(task.getUserName());
        int dbAmount = igUserService.findUserByIgUserName(task.getUserName()).getFollowerCount();
        log.info("任務:{} ,取追蹤者數量:{},資料庫追蹤者數量:{}", task, dbAmount, crawlerAmount);
        return FollowerCrawlingUtil.isCrawlingCloseToRealFollowerCount(crawlerAmount, dbAmount);
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
        //更新登入帳號狀態為已使用
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
