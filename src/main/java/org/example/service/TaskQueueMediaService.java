package org.example.service;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueMedia;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/3/8
 */
public interface TaskQueueMediaService extends BaseService<TaskQueueMedia> {
    /**
     * 儲存全部
     *
     * @param taskQueueList 任務列表
     * @return 任務列表
     */
    List<TaskQueueMedia> saveAll(List<TaskQueueMedia> taskQueueList);

    /**
     * 透過任務參數，取得一個影片任務明細
     *
     * @param taskQueue 任務
     * @param status    任務狀態
     * @return 任務列表
     */
    Optional<TaskQueueMedia> findByTaskQueueAndStatus(TaskQueue taskQueue, TaskStatusEnum status);

    /**
     * 刪除任務
     *
     * @param taskQueue 任務
     */
    void deleteByTaskQueue(TaskQueue taskQueue);
}
