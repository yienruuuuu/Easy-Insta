package org.example.service;

import org.example.entity.IgUser;

public interface InstagramService {
    /**
     * 登入
     *
     * @param account 登入操作用帳號
     * @param password 登入操作用密碼
     */
    void login(String account, String password);

    /**
     * 以用戶名查詢用戶
     *
     * @param username        用戶名
     * @return 用戶信息
     */
    IgUser searchUser(String username);

    /**
     * 查詢用戶追隨者
     *
     * @param username 用戶名
     */
    void searchUserFollowers(String username);

    /**
     * 查詢用戶發文
     *
     * @param username 用戶名
     */
    void searchUserPosts(String username);
}
