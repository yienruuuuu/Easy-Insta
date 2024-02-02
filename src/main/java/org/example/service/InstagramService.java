package org.example.service;

import org.example.bean.enumtype.AccountEnum;

public interface InstagramService {
    /**
     * 登入
     *
     * @param account 登入操作用帳號
     */
    public void login(AccountEnum account);

}
