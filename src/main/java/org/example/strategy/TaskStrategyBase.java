package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.exception.TaskExecutionException;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.example.service.TaskQueueService;
import org.springframework.stereotype.Service;

/**
 * @author Eric.Lee
 * Date: 2024/2/27
 */
@Slf4j
@Service("taskStrategyBase")
public abstract class TaskStrategyBase implements TaskStrategy {
    protected final InstagramService instagramService;
    protected final LoginService loginService;

    protected TaskStrategyBase(InstagramService instagramService, LoginService loginService) {
        this.instagramService = instagramService;
        this.loginService = loginService;
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
}
