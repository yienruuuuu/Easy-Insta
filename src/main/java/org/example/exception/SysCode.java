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

    IG_USER_NOT_FOUND(3000, "IG user not found"),
    NOT_FOUND(7000, "Data not found"),
    ERROR(9999, "Unexpected error");

    private final Integer code;
    private final String message;

}