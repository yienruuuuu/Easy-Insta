package org.example.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.user.Profile;
import org.example.entity.IgUser;
import org.example.entity.TaskQueue;

import java.util.List;

/**
 * Instagram操作相關Service
 *
 * @Author: Eric.Lee
 */
public interface InstagramService {
    /**
     * 登入
     *
     * @param account  登入操作用帳號
     * @param password 登入操作用密碼
     */
    boolean login(String account, String password);

    /**
     * 以用戶名查詢用戶
     *
     * @param username 用戶名
     * @return 用戶信息
     */
    IgUser searchUser(String username);

    /**
     * 查詢用戶追隨者
     *
     * @param task 用戶名
     */
    boolean searchTargetUserFollowersAndSave(TaskQueue task, String maxCount);

    /**
     * 查詢用戶發文
     *
     * @param username 用戶名
     */
    void searchUserPosts(String username);

}