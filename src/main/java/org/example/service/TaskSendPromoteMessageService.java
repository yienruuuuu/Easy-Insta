package org.example.service;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.entity.TaskSendPromoteMessage;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/2/26
 */
public interface TaskSendPromoteMessageService {
    /**
     * 批量保存
     *
     * @param taskSendPromoteMessageList 任務列表
     */
    void saveAll(List<TaskSendPromoteMessage> taskSendPromoteMessageList);

    List<TaskSendPromoteMessage> findByTaskQueueAndStatus(TaskQueue taskQueue, TaskStatusEnum status, Pageable pageable);
}
