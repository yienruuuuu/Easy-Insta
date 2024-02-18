package org.example.task;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Eric.Lee
 * Date:2024/2/19
 */
@Slf4j
public abstract class BaseQueue {
    private volatile boolean enableTask = true;

    public void stopTasks() {
        this.enableTask = false;
    }

    public boolean isTaskEnabled() {
        return this.enableTask;
    }

    // 在Base Queue中加入一個方法，這個方法檢查任務是否啟用，並記錄日誌
    protected boolean checkTaskEnabled() {
        if (!isTaskEnabled()) {
            log.info("任務調度已停止");
            return false;
        }
        return true;
    }
}
