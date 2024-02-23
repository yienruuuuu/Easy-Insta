package org.example.service.impl;

import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.dao.LoginAccountDao;
import org.example.entity.LoginAccount;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service("loginService")
public class LoginServiceImpl implements LoginService {
    @Autowired
    LoginAccountDao loginAccountDao;

    @Override
    public Optional<LoginAccount> save(LoginAccount target) {
        return Optional.of(loginAccountDao.save(target));
    }

    @Override
    public Optional<LoginAccount> findById(Integer id) {
        return Optional.of(loginAccountDao.findById(id).get());
    }

    @Override
    public List<LoginAccount> findAll() {
        return loginAccountDao.findAll();
    }

    @Override
    public Optional<LoginAccount> findFirstLoginAccountByStatus(LoginAccountStatusEnum status) {
        return loginAccountDao.findFirstByByStatus(status);
    }

    @Override
    public int updateExhaustedAccounts(LocalDateTime thresholdTime, LoginAccountStatusEnum newStatus, LoginAccountStatusEnum oldStatus) {
        return loginAccountDao.updateStatusForExhaustedAccountsBefore(thresholdTime, newStatus, oldStatus);
    }
}
