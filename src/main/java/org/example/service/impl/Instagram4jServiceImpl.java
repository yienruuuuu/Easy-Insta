package org.example.service.impl;


import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.user.User;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.AccountEnum;
import org.example.entity.IgUser;
import org.example.entity.LoginAccount;
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

    private IGClient client;

    @PostConstruct
    public void init() {
        Optional<LoginAccount> optionalAccount = loginService.findById(AccountEnum.ERICLEE09578.getLoginAccountId());
        optionalAccount.ifPresentOrElse(
                loginAccount -> login(loginAccount.getAccount(), loginAccount.getPassword()),
                () -> log.error("ERICLEE09578 帳號不存在")
        );
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
    public IgUser searchUser(String username) {
        UserAction searchResult = client.actions().users().findByUsername(username).join();
        User igUser = searchResult.getUser();
        log.info("IG查詢結果,用戶名稱: {} ,查詢用戶PK: {}", igUser.getUsername(), igUser.getPk());
        // 查找数据库中是否已存在该用户
        Optional<IgUser> userOptional = igUserService.findUserByIgPk(igUser.getPk());
        // 创建新的 User 实体或从数据库获取已存在的
        return getIgUser(userOptional, igUser);
    }

    @Override
    public void searchUserFollowers(String username) {

    }

    @Override
    public void searchUserPosts(String username) {

    }

    //private

    //資料實體處理
    @NotNull
    private static IgUser getIgUser(Optional<IgUser> userOptional, User igUser) {
        IgUser userEntity = userOptional.orElse(new IgUser());

        // 设置或更新用户信息
        userEntity.setIgPk(igUser.getPk());
        userEntity.setUserName(igUser.getUsername());
        userEntity.setFullName(igUser.getFull_name());
        userEntity.setMediaCount(igUser.getMedia_count());
        userEntity.setFollowerCount(igUser.getFollower_count());
        userEntity.setFollowingCount(igUser.getFollowing_count());
        return userEntity;
    }

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
