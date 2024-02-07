package org.example.service.impl;


import com.github.instagram4j.instagram4j.IGClient;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.AccountEnum;
import org.example.entity.IgUser;
import org.example.entity.LoginAccount;
import org.example.service.InstagramService;
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

    private IGClient client;

    @PostConstruct
    public void init() {
        // 初始化 IGClient 預設使用ERICLEE09578登入
        Optional<LoginAccount> password = loginService.findById(AccountEnum.ERICLEE09578.getLoginAccountId());
        if (!password.isPresent()) {
            log.error("ERICLEE09578 帳號不存在");
            return;
        }
        login(AccountEnum.ERICLEE09578.name(), password.get().getPassword());

    }

    @Override
    public void login(String account, String password) {
        try {
            client = IGClient.builder()
                    .username(account)
                    .password(password)
                    .login();
            log.info("登入成功, 帳號:{}", account);
        } catch (Exception e) {
            log.error("登入異常", e);
        }
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
