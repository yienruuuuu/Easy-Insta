package org.example.service.impl;


import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.user.User;
import com.xcoder.easyinsta.Instagram;
import com.xcoder.easyinsta.Utils;
import com.xcoder.easyinsta.models.UserInfo;
import com.xcoder.tasks.AsyncTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.AccountEnum;
import org.example.entity.IgUser;
import org.example.service.InstagramService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Service("instagramService")
public class Instagram4jServiceImpl implements InstagramService {
    @Autowired
    LoginServiceImpl loginService;
    @Autowired
    IgUserServiceImpl igUserService;

    @Getter
    private IGClient client;
    @Getter
    private Instagram instagram;

    @PostConstruct
    public void init() {
        // 初始化 IGClient 預設使用ERICLEE09578登入
        login(AccountEnum.ERICLEE09578);
    }

    @Override
    public void login(AccountEnum account) {
        loginService.findById(account.getLoginAccountId()).ifPresent(login -> {
            try {
                instagram = Instagram.login(login.getAccount(), login.getPassword());
                log.info("登入成功 : {}", instagram.username);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public IgUser searchUser(String username, boolean needToWriteToDb) {
        try {
            String aaa = Utils.getPkFromUsername("marianlinlin");
            AsyncTask<UserInfo> task = instagram.profile().getUserInfo("marianlinlin");
            task.setOnSuccessCallback(result -> {

                System.out.println("User info: " + printUserInfo(result));
            });
            System.out.println("Task started aaa = " + aaa);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //private

    //資料實體處理
    @NotNull
    private static IgUser getIgUserService(Optional<IgUser> userOptional, User igUser) {
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

    private String printUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            System.out.println("UserInfo is null");
            return "UserInfo is null";
        }

        String info = "UserInfo{\n" +
                "username='" + userInfo.username + "',\n" +
                "fullName='" + userInfo.fullName + "',\n" +
                "biography='" + userInfo.biography + "',\n" +
                "profilePicUrl='" + userInfo.profilePicUrl + "',\n" +
                "followers=" + userInfo.followers + ",\n" +
                "following=" + userInfo.following + ",\n" +
                "posts=" + userInfo.posts + "\n" +
                '}';
        return info;
    }

}
