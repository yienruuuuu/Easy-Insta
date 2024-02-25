package org.example.task;

import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;

/**
 * @author Eric.Lee
 * Date:2024/2/19
 */
public interface TaskExecutionService {
    /**
     * 執行取得追蹤者任務
     *
     * @param task 任務
     */
    void executeGetFollowerTask(TaskQueue task , LoginAccount loginAccount);
}
