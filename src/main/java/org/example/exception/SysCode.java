package org.example.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Eric.Lee
 */

@Getter
@AllArgsConstructor
public enum SysCode implements ErrorCode {

    OK(1000, "成功"),
    FAIL(1001, "Expected error"),

    //程式運行相關錯誤
    CONFIG_NOT_FOUND(2000, "配置未找到"),
    NO_AVAILABLE_LOGIN_ACCOUNT(2001, "沒有可用的登錄帳戶"),
    NO_TASKS_TO_PERFORM(2002, "沒有需要執行的任務"),
    FOLLOWERS_OR_MEDIA_AMOUNT_IS_ZERO(2003, "粉絲數和貼文數必須大於0"),
    FILE_UPLOAD_FAILED(2004, "文件上傳失敗"),

    //任務運行相關錯誤
    TASK_CREATION_FAILED(2100, "任務創建失敗"),
    TASK_TYPE_NOT_FOUND(2101, "任務類型未找到"),
    TASK_CONFIG_NOT_FOUND(2102, "任務配置未找到"),
    TASK_TYPE_NOT_FOUND_IN_STRATEGY_FACTORY(2103, "策略工廠中找不到任務類型"),
    TASK_ALREADY_EXISTS(2104, "對於該查詢對象，任務已存在，請耐心等候"),
    TASK_STATUS_UPDATE_FAILED(2105, "任務狀態更新失敗，樂觀鎖阻止了任務狀態的更新"),
    TASK_QUEUE_MEDIA_NOT_FOUND(2106, "找不到任務對應的媒體任務明細"),
    TASK_WAIT_SELENIUM_TIME_OUT(2107, "等待selenium超時"),

    //IG行為時相關錯誤
    IG_USER_NOT_FOUND(2200, "IG user not found"),
    IG_LOGIN_FAILED(2201, "登錄失敗"),
    IG_GET_FOLLOWERS_FAILED(2202, "IG獲取追蹤者失敗"),
    IG_USER_NOT_FOUND_IN_DB(2203, "IG用戶在資料庫中找不到"),
    IG_GET_MEDIA_FAILED(2204, "IG獲取貼文失敗"),
    IG_GET_COMMENTS_FAILED(2205, "IG獲取貼文留言失敗"),
    IG_ACCOUNT_CHALLENGE_REQUIRED(2206, "IG帳戶需要進行人機驗證，放棄使用"),
    IG_GET_LIKERS_FAILED(2207, "IG獲取貼文按讚者失敗"),


    //分析相關錯誤,
    MEDIA_NOT_FOUND(2301, "未找到貼文資訊，請確認已跑過貼文排程"),
    ACTUAL_COUNT_IS_ZERO(2302, "實際數量為0"),

    //其他錯誤
    NOT_FOUND(7000, "Data not found"),
    SOCKET_TIMEOUT(7001, "Socket timeout"),
    ERROR(9999, "Unexpected error");

    private final Integer code;
    private final String message;
}