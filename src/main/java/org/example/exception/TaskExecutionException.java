package org.example.exception;

import lombok.Getter;

/**
 * @author Eric.Lee
 * Date:2024/2/22
 */
@Getter
public class TaskExecutionException extends RuntimeException {
    private final ErrorCode code;

    // 預設建構子
    public TaskExecutionException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    // 帶有錯誤消息的建構子
    public TaskExecutionException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    // 帶有錯誤訊息和導致異常的根本原因的建構函數
    public TaskExecutionException(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    // 帶有導致異常的根本原因的建構函數
    public TaskExecutionException(ErrorCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
