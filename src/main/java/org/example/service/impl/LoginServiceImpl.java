package org.example.service.impl;

import org.example.dao.LoginAccountDao;
import org.example.entity.LoginAccount;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
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
    public Optional<LoginAccount> findById(BigInteger id) {
        return Optional.of(loginAccountDao.findById(id).get());
    }

    @Override
    public List<LoginAccount> findAll() {
        return loginAccountDao.findAll();
    }
}
