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
    MAX_FOLLOWERS_PER_REQUEST,
    //查詢貼文留言，每次請求最大數量
    MAX_COMMENTS_PER_REQUEST,
    //查詢貼文按讚，每次請求最大數量
    MAX_LIKERS_PER_REQUEST,
    //selenuim 驗證畫面是否已達任務前準備
    SELENIUM_IG_VIEW_FANS_SEARCH_STYLE,
    //selenuim 抓取輸入框
    SELENIUM_IG_INPUT_STYLE,
    //selenuim 抓取追蹤者明細資料
    SELENIUM_IG_FOLLOWERS_DATA_STYLE,
    //selenuim 準備發送訊息
    SELENIUM_IG_READY_FOR_SEND_MESSAGE,
    //selenuim 以js找尋新訊息svg下的title標籤
    SELENIUM_IG_SEND_MESSAGE_GET_TITLE_BY_JS,
    //selenuim 以影片分享鍵進行推廣的每日上限
    MAX_PROMOTION_BY_POST_SHARE_PER_DAY
}
