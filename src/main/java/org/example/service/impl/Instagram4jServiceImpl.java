package org.example.service.impl;


import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import org.example.bean.enumtype.AccountEnum;
import org.example.service.InstagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("instagramService")
public class Instagram4jServiceImpl implements InstagramService {
    @Autowired
    LoginServiceImpl loginService;

    private IGClient client;

    @PostConstruct
    public void init() {
        // 初始化 IGClient 預設使用ERICLEE09578登入
        login(AccountEnum.ERICLEE09578);
    }

    @Override
    public void login(AccountEnum account) {
        loginService.findById(account.getLoginAccountId()).ifPresent(login -> {
            try {
                client = IGClient.builder()
                        .username(login.getAccount())
                        .password(login.getPassword())
                        .login();
                System.out.println("登入成功 : " + client.getSelfProfile().getUsername());
            } catch (IGLoginException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
