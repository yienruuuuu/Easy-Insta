package org.example.task;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Eric.Lee
 * Date:2024/2/19
 */
@Slf4j
public abstract class BaseQueue {
    private volatile boolean enableTask = true;

    public void stopBaseQueue() {
        this.enableTask = false;
    }

    public boolean isTaskEnabled() {
        return this.enableTask;
    }

    /**
     * 檢查根任務是否啟用
     *
     * @return 是否啟用
     */
    protected boolean checkTaskEnabled() {
        if (!isTaskEnabled()) {
            log.info("任務調度已停止");
            return false;
        }
        return true;
    }
}
