package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueMedia;
import org.example.exception.SysCode;
import org.example.exception.TaskExecutionException;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.example.service.TaskQueueMediaService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/27
 */
@Slf4j
@Service("taskStrategyBase")
public abstract class TaskStrategyBase implements TaskStrategy {
    protected final InstagramService instagramService;
    protected final LoginService loginService;
    protected final TaskQueueMediaService taskQueueMediaService;

    protected TaskStrategyBase(InstagramService instagramService, LoginService loginService, TaskQueueMediaService taskQueueMediaService) {
        this.instagramService = instagramService;
        this.loginService = loginService;
        this.taskQueueMediaService = taskQueueMediaService;
    }

    protected void loginAndUpdateAccountStatus(LoginAccount loginAccount) {
        try {
            instagramService.login(loginAccount.getAccount(), loginAccount.getPassword());
        } catch (TaskExecutionException e) {
            handleLoginFailure(loginAccount, e);
            throw new TaskExecutionException(SysCode.IG_LOGIN_FAILED, e);
        }
        //更新登入帳號狀態為已使用
        loginAccount.loginAccountExhausted();
        loginService.save(loginAccount);
    }

    /**
     * 處理登入失敗
     */
    private void handleLoginFailure(LoginAccount loginAccount, TaskExecutionException e) {
        log.error("登入失敗，帳號:{} ,更新帳號狀態，錯誤詳情: {}", loginAccount, e.getMessage());
        loginAccount.loginAccountDeviant(e.getMessage());
        loginService.save(loginAccount);
    }

    /**
     * 找出一個當前應執行的taskQueueMedia，有優先順序的條件為PAUSED>PENDING，如果都沒有則拋出例外
     *
     * @param taskQueue 任務
     */
    protected Optional<TaskQueueMedia> getTaskQueueMediaWhichIsPausedOrPending(TaskQueue taskQueue) {
        return taskQueueMediaService.findByTaskQueueAndStatus(taskQueue, TaskStatusEnum.PAUSED)
                .or(() -> taskQueueMediaService.findByTaskQueueAndStatus(taskQueue, TaskStatusEnum.PENDING));
    }

    /**
     * 根據條件更新任務狀態
     *
     * @param task 任務
     */
    protected void updateTaskStatusBasedOnCondition(TaskQueue task) {
        //若TaskQueueMedia.nextMediaId不為null，代表仍需繼續查詢，僅暫停任務
        if (task.getTaskQueueMediaId().getNextMediaId() != null) {
            task.pauseTask();
            return;
        }
        //若TaskQueueMedia.nextMediaId為null，代表該貼文已查詢完畢，更新子任務狀態為已完成
        task.getTaskQueueMediaId().setStatus(TaskStatusEnum.COMPLETED);
        //若TaskQueueMedia.nextMediaId為null，代表已查詢完畢，更新下一筆子任務指針，並暫停任務等待繼續
        getTaskQueueMediaWhichIsPausedOrPending(task).ifPresentOrElse(taskQueueMedia -> {
            task.updateTaskQueueMedia(taskQueueMedia);
            task.pauseTask();
        }, task::completeTask);
    }
}
