package org.example.service;

import org.example.bean.enumtype.AccountEnum;
import org.example.entity.IgUser;

public interface InstagramService {
    /**
     * 登入
     *
     * @param account 登入操作用帳號
     */
    void login(AccountEnum account);

    /**
     * 查詢用戶
     *
     * @param username        用戶名
     * @param needToWriteToDb 是否需要寫入數據庫
     * @return 用戶信息
     */
    IgUser searchUser(String username, boolean needToWriteToDb);

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
