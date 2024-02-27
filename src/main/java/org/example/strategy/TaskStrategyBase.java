package org.example.strategy;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.LoginAccount;
import org.example.exception.TaskExecutionException;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Eric.Lee
 * Date: 2024/2/27
 */
@Slf4j
@Service("taskStrategyBase")
public abstract class TaskStrategyBase implements TaskStrategy {
    @Autowired
    InstagramService instagramService;
    @Autowired
    LoginService loginService;

    protected void loginAndUpdateAccountStatus(LoginAccount loginAccount) {
        try {
            instagramService.login(loginAccount.getAccount(), loginAccount.getPassword());
        } catch (TaskExecutionException e) {
            handleLoginFailure(loginAccount, e);
            throw new TaskExecutionException("登入失敗");
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
