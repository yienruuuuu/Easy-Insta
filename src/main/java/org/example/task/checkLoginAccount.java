package org.example.task;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Eric.Lee
 * Date:2024/2/20
 */
@Slf4j
@Service
public class checkLoginAccount {
    @Autowired
    private LoginService loginService;

    @Value("${exhausted.account.resurrection.coldtime:2}")
    private int accountColdtime;

    // 每小時執行一次
    @Scheduled(fixedDelayString = "${taskQueue.checkDelay:10000}")
    public void updateExhaustedAccounts() {
        log.info("開始檢查並更新登入帳號狀態");
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(accountColdtime);
        // 呼叫service層方法來更新狀態
        int updatedCount = loginService.updateExhaustedAccounts(oneHourAgo, LoginAccountStatusEnum.NORMAL, LoginAccountStatusEnum.EXHAUSTED);
        log.info("已更新 {} 個登入帳號狀態為 NORMAL", updatedCount);
    }
}