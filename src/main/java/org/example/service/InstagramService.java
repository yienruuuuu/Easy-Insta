package org.example.service;

import org.example.entity.IgUser;
import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;

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
    void login(String account, String password);

    /**
     * 以用戶名查詢用戶
     *
     * @param username 用戶名
     * @return 用戶信息
     */
    IgUser searchUser(String username , LoginAccount loginAccount);

    /**
     * 透過Instagram4JApi 查詢用戶追隨者
     *
     * @param task  任務資訊
     *              task.getUserId() 查詢對象Id
     * @param maxId 最大查詢數量 初次執行可能為null
     *              之後執行需帶入最後一次查詢的最後一筆Id
     * @return 是否查詢成功
     */
    void searchTargetUserFollowersAndSave(TaskQueue task, String maxId);

    /**
     * 查詢用戶發文
     *
     * @param username 用戶名
     */
    void searchUserPostsByTargetUserNameAndSave(TaskQueue task, String maxId);

}