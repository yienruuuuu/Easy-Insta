package org.example.task.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.example.service.TaskQueueService;
import org.example.task.BaseQueue;
import org.example.task.TaskExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    public void executeGetFollowerTask(TaskQueue task) {
        log.info("開始執行任務: {}", task);
        String maxId = task.getNextIdForSearch() == null ? null : task.getNextIdForSearch();
        loginAndCheckResult(getLoginAccount(), task);

        boolean result = instagramService.searchTargetUserFollowersAndSave(task, maxId);
        if (!result) {
            log.error("取得追蹤者失敗，任務終止: {}", task);
            stopTasks();
            return;
        }

        if (task.getNextIdForSearch() == null) {
            task.setStatus(TaskStatusEnum.COMPLETED);
            task.setEndTime(LocalDateTime.now());
        } else {
            task.setStatus(TaskStatusEnum.PAUSED);
        }
        taskQueueService.save(task);
        log.info("任務完成:{}", task);
    }

    //private

    /**
     * 從資料庫中取得一個可用的登入帳號
     *
     * @return 可用的登入帳號
     */
    private LoginAccount getLoginAccount() {
        return loginService.findLoginAccountByStatus(LoginAccountStatusEnum.NORMAL)
                .orElseThrow(() -> new RuntimeException("目前沒有可用的登入帳號"));
    }

    /**
     * 登入並檢查結果
     *
     * @param loginAccount 登入帳號
     * @param task         任務
     */
    private void loginAndCheckResult(LoginAccount loginAccount, TaskQueue task) {
        boolean loginResult = instagramService.login(loginAccount.getAccount(), loginAccount.getPassword());
        if (!loginResult) {
            loginAccount.setStatus(LoginAccountStatusEnum.DEVIANT);
            loginAccount.setStatusRemark(String.valueOf(LocalDateTime.now()));
            log.info("登入失敗，任務終止: {}, 帳號:{}", task, loginAccount);
            loginService.save(loginAccount);
        }
    }
}
