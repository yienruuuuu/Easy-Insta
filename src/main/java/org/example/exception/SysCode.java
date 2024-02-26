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

    FAIL(2000, "Expected error"),
    TASK_ALREADY_EXISTS(2001, "對於該查詢對象，Followers查詢任務已存在，請耐心等候"),
    TASK_CREATION_FAILED(2002, "任務創建失敗"),
    IG_GET_FOLLOWERS_FAILED(2003, "IG獲取追蹤者失敗"),
    TASK_STATUS_UPDATE_FAILED(2004, "任務狀態更新失敗，樂觀鎖阻止了任務狀態的更新"),
    NO_AVAILABLE_LOGIN_ACCOUNT(2005, "沒有可用的登錄帳戶"),
    NO_TASKS_TO_PERFORM(2006, "沒有任務可執行"),
    ACTUAL_COUNT_IS_ZERO(2007, "實際數量為0"),
    IG_USER_NOT_FOUND_IN_DB(2008, "IG用戶在資料庫中找不到"),
    TASK_CONFIG_NOT_FOUND(2009, "任務配置未找到"),

    IG_USER_NOT_FOUND(3000, "IG user not found"),
    NOT_FOUND(7000, "Data not found"),
    ERROR(9999, "Unexpected error");

    private final Integer code;
    private final String message;

}