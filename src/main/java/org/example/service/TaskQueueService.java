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
    boolean checkTaskQueueExistByUserAndTaskType(IgUser targetUser, TaskTypeEnum taskType);

    /**
     * 創建任務佇列並刪除舊數據(依任務類型)
     *
     * @param igUser   用戶
     * @param taskType 任務類型
     * @param status   任務狀態
     * @return TaskQueue
     */
    TaskQueue createTaskQueueAndDeleteOldData(IgUser igUser, TaskTypeEnum taskType, TaskStatusEnum status);

    boolean checkTasksByStatusAndNeedLogin(List<TaskStatusEnum> status, boolean needLoginIg);

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
