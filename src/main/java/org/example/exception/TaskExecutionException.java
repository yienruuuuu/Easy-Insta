package org.example.exception;

/**
 * @author Eric.Lee
 * Date:2024/2/22
 */
public class TaskExecutionException extends RuntimeException {
    // 預設建構子
    public TaskExecutionException() {
        super();
    }

    // 帶有錯誤消息的建構子
    public TaskExecutionException(String message) {
        super(message);
    }

    // 帶有錯誤訊息和導致異常的根本原因的建構函數
    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    // 帶有導致異常的根本原因的建構函數
    public TaskExecutionException(Throwable cause) {
        super(cause);
    }
}
