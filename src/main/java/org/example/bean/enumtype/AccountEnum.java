package org.example.bean.enumtype;

import lombok.Getter;

@Getter
public enum AccountEnum {
    /**
     * 登入操作用帳號
     */
    ERICLEE09578(0);

    private final Integer loginAccountId;

    AccountEnum(Integer loginAccountId) {
        this.loginAccountId = loginAccountId;
    }

}
