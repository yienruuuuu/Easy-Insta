package org.example.service;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.IgUser;
import org.example.entity.TaskQueue;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
public interface TaskQueueService extends BaseService<TaskQueue> {
    boolean checkGetFollowersTaskQueueExist(IgUser targetUser, TaskTypeEnum taskType);

    Optional<TaskQueue> createTaskQueueAndDeleteOldData(IgUser igUser, TaskTypeEnum taskType, TaskStatusEnum status);

    boolean checkTasksByStatusAndNeedLogin(TaskStatusEnum status, boolean needLoginIg);

    Optional<TaskQueue> findFirstTaskQueueByStatusAndNeedLogin(TaskStatusEnum status, boolean needLoginIg);

    List<TaskQueue> findTasksByStatus(TaskStatusEnum status);

    /**
     * 更新任務狀態(採用樂觀鎖)
     *
     * @param taskId    任務ID
     * @param newStatus 新狀態
     * @return boolean
     */
    TaskQueue updateTaskStatus(BigInteger taskId, TaskStatusEnum newStatus);
}
