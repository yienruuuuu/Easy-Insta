package org.example.task;

import org.example.entity.TaskQueue;

/**
 * @author Eric.Lee
 * Date:2024/2/19
 */
public interface TaskExecutionService {
    void executeGetFollowerTask(TaskQueue task);
}
