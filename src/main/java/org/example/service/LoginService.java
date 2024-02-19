package org.example.service;


import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.entity.LoginAccount;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginService extends BaseService<LoginAccount> {
    Optional<LoginAccount> findLoginAccountByStatus(LoginAccountStatusEnum status);

    int updateExhaustedAccounts(LocalDateTime thresholdTime, LoginAccountStatusEnum newStatus, LoginAccountStatusEnum oldStatus);
}