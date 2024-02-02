package org.example.service.impl;


import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.user.User;
import lombok.Getter;
import org.example.bean.enumtype.AccountEnum;
import org.example.entity.IgUser;
import org.example.service.InstagramService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service("instagramService")
public class Instagram4jServiceImpl implements InstagramService {
    @Autowired
    LoginServiceImpl loginService;
    @Autowired
    IgUserImpl userService;

    @Getter
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

    @Override
    public IgUser searchUser(String username, boolean needToWriteToDb) {
        //搜尋
        UserAction searchResult = client.actions().users().findByUsername(username).join();
//        获取 User 对象User
        User igUser = searchResult.getUser();
        System.out.println("查詢用戶 : " + igUser.getUsername());
        System.out.println("查詢用戶PK : " + igUser.getPk());
        // 查找数据库中是否已存在该用户
        Optional<IgUser> userOptional = userService.findUserByIgPk(igUser.getPk());

        // 创建新的 User 实体或从数据库获取已存在的
        IgUser userEntity = getIgUser(userOptional, igUser);

        // 检查是否需要写入数据库
        if (needToWriteToDb) {
            // 保存或更新用户信息
            userService.save(userEntity);
            System.out.println("用户信息已保存或更新 : " + userEntity.getUserName());
        } else {
            System.out.println("不需要写入数据库 : " + userEntity.getUserName());
        }
        return userEntity;

    }

    @NotNull
    private static IgUser getIgUser(Optional<IgUser> userOptional, User igUser) {
        IgUser userEntity = userOptional.orElse(new IgUser());

        // 设置或更新用户信息
        userEntity.setIgPk(igUser.getPk());
        userEntity.setUserName(igUser.getUsername());
        userEntity.setFullName(igUser.getFull_name());
        userEntity.setPrivate(igUser.is_private());
        userEntity.setBusiness(igUser.is_business());
        userEntity.setMediaCount(igUser.getMedia_count());
        userEntity.setFollowerCount(igUser.getFollower_count());
        userEntity.setFollowingCount(igUser.getFollowing_count());
        userEntity.setIgAccountType(igUser.getAccount_type());
        return userEntity;
    }
}
