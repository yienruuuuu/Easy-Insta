package org.example.service;


import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.entity.LoginAccount;

import java.util.Optional;

public interface LoginService extends BaseService<LoginAccount> {
        Optional<LoginAccount> findLoginAccountByStatus(LoginAccountStatusEnum status);
}