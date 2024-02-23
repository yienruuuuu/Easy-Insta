package org.example.service;


import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.entity.LoginAccount;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginService extends BaseService<LoginAccount> {
    /**
     * 從資料庫中取得一個可用的登入帳號
     *
     * @return 可用的登入帳號
     */
    Optional<LoginAccount> findFirstLoginAccountByStatus(LoginAccountStatusEnum status);

    /**
     * 更新所有在指定時間之前的狀態為oldStatus的帳號為newStatus
     *
     * @param thresholdTime 時間閾值
     * @param newStatus     新狀態
     * @param oldStatus     舊狀態
     * @return 更新筆數
     */
    int updateExhaustedAccounts(LocalDateTime thresholdTime, LoginAccountStatusEnum newStatus, LoginAccountStatusEnum oldStatus);
}