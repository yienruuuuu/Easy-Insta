package org.example.bean.enumtype;

import lombok.Getter;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
@Getter
public enum ConfigEnum {
    //proxy帳密
    BRIGHT_DATA_ACCOUNT,
    BRIGHT_DATA_PASSWORD,
    //查詢追蹤者，每次請求最大數量
    MAX_POSTS_PER_REQUEST,
    //查詢貼文總覽，每次請求最大數量
    MAX_FOLLOWERS_PER_REQUEST;
}
