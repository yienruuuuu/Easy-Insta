package org.example.dao;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.entity.TaskSendPromoteMessage;
import org.example.entity.TaskSendPromoteMessagePK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskSendPromoteMessageDao extends JpaRepository<TaskSendPromoteMessage, TaskSendPromoteMessagePK> {
    /**
     * 根據任務和狀態查詢
     *
     * @param taskQueue 任務
     * @param status    狀態
     * @return 任務列表
     */
    List<TaskSendPromoteMessage> findByTaskQueueAndStatus(TaskQueue taskQueue, TaskStatusEnum status);
}