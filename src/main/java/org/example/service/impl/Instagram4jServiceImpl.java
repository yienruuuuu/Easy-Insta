package org.example.service.impl;


import com.xcoder.easyinsta.Instagram;
import com.xcoder.easyinsta.Utils;
import com.xcoder.easyinsta.models.Post;
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
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("instagramService")
public class Instagram4jServiceImpl implements InstagramService {
    @Autowired
    LoginServiceImpl loginService;
    @Autowired
    IgUserServiceImpl igUserService;

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
        IgUser userEntity = null;
        try {
            long igUserPk = Long.parseLong(Utils.getPkFromUsername(username));
            AsyncTask<UserInfo> task = instagram.profile().getUserInfo(username).setOnSuccessCallback(userInfo -> log.info("userInfo : {}", printUserInfo(userInfo)));
            UserInfo userInfo = task.getResult(5);
            Optional<IgUser> igUserFromDb = igUserService.findUserByIgPk(igUserPk);
            userEntity = getIgUserService(igUserFromDb, userInfo, igUserPk, username);
            if (needToWriteToDb) {
                igUserService.save(userEntity);
            }
        } catch (Exception e) {
            log.error("searchUser error : {}", e.getMessage());
        }
        return userEntity;
    }

    @Override
    public void searchUserFollowers(String username) {
        try {
            AsyncTask<List<String>> followersTask = instagram.profile().getFollowers(username);
            followersTask.setOnSuccessCallback(followers -> {
                log.info("Followers of {}: {}", username, followers);
            });
            List<String> followers = followersTask.getResult(20);
        } catch (Exception e) {
            log.error("Error fetching followers of {}: {}", username, e.getMessage());
        }
    }

    @Override
    public void searchUserPosts(String username) {
        try {
            AsyncTask<Post[]> postsTask = instagram.profile().getPosts(username);
            postsTask.setOnSuccessCallback(posts -> {
                log.info("Posts of {}: {}", username, posts);
            });
            Post[] posts = postsTask.getResult(20);
        } catch (Exception e) {
            log.error("Error fetching posts of {}: {}", username, e.getMessage());
        }
    }

    //private
    //資料實體處理
    @NotNull
    private static IgUser getIgUserService(Optional<IgUser> userOptional, UserInfo userInfo, Long igUserPk, String username) {
        IgUser userEntity = userOptional.orElse(new IgUser());

        // 设置或更新用户信息
        userEntity.setIgPk(igUserPk);
        userEntity.setUserName(username);
        userEntity.setFullName(userInfo.fullName);
        userEntity.setMediaCount(userInfo.posts);
        userEntity.setFollowerCount(userInfo.followers);
        userEntity.setFollowingCount(userInfo.following);
        log.info("userEntity : {}", userEntity);
        return userEntity;
    }

    private String printUserInfo(UserInfo userInfo) {
        if (userInfo == null) {
            log.info("UserInfo is null");
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
