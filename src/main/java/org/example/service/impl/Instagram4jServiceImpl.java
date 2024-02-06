package org.example.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.AccountEnum;
import org.example.entity.IgUser;
import org.example.service.InstagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service("instagramService")
public class Instagram4jServiceImpl implements InstagramService {
    @Autowired
    LoginServiceImpl loginService;
    @Autowired
    IgUserServiceImpl igUserService;

    @PostConstruct
    public void init() {
        // 初始化 IGClient 預設使用ERICLEE09578登入
        login(AccountEnum.ERICLEE09578);
    }

    @Override
    public void login(AccountEnum account) {

    }

    @Override
    public IgUser searchUser(String username, boolean needToWriteToDb) {
        return null;
    }

    @Override
    public void searchUserFollowers(String username) {

    }

    @Override
    public void searchUserPosts(String username) {

    }

    //private
//    //資料實體處理
//    @NotNull
//    private static IgUser getIgUserService(Optional<IgUser> userOptional, UserInfo userInfo, Long igUserPk, String username) {
//        IgUser userEntity = userOptional.orElse(new IgUser());
//
//        // 设置或更新用户信息
//        userEntity.setIgPk(igUserPk);
//        userEntity.setUserName(username);
//        userEntity.setFullName(userInfo.fullName);
//        userEntity.setMediaCount(userInfo.posts);
//        userEntity.setFollowerCount(userInfo.followers);
//        userEntity.setFollowingCount(userInfo.following);
//        log.info("userEntity : {}", userEntity);
//        return userEntity;
//    }
//
//    private String printUserInfo(UserInfo userInfo) {
//        if (userInfo == null) {
//            log.info("UserInfo is null");
//            return "UserInfo is null";
//        }
//
//        String info = "UserInfo{\n" +
//                "username='" + userInfo.username + "',\n" +
//                "fullName='" + userInfo.fullName + "',\n" +
//                "biography='" + userInfo.biography + "',\n" +
//                "profilePicUrl='" + userInfo.profilePicUrl + "',\n" +
//                "followers=" + userInfo.followers + ",\n" +
//                "following=" + userInfo.following + ",\n" +
//                "posts=" + userInfo.posts + "\n" +
//                '}';
//        return info;
//    }
}
