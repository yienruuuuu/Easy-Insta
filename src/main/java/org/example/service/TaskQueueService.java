package org.example.service;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.TaskQueue;

import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
public interface TaskQueueService extends BaseService<TaskQueue> {
    boolean checkGetFollowersTaskQueueExist(String userId, TaskTypeEnum taskType);

    Optional<TaskQueue> createAndSaveTaskQueue(String username, TaskTypeEnum taskType, TaskStatusEnum status);

}
