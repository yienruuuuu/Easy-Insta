package org.example.strategy;

import org.example.entity.LoginAccount;
import org.example.entity.TaskQueue;

/**
 * @author Eric.Lee
 * Date: 2024/2/27
 */
public interface TaskStrategy {
    void executeTask(TaskQueue taskQueue, LoginAccount loginAccount);
}
