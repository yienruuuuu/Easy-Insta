package org.example.service;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueFollowersDetail;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/8
 */
public interface TaskQueueFollowerDetailService extends BaseService<TaskQueueFollowersDetail> {
    /**
     * 儲存全部
     *
     * @param taskQueueList 任務列表
     */
    void saveAll(List<TaskQueueFollowersDetail> taskQueueList);

    /**
     * 透過狀態查詢任務
     *
     * @param status    狀態
     * @param taskQueue 任務隊列
     * @param page      頁碼
     * @param size      每頁筆數
     * @return 任務列表
     */
    Page<TaskQueueFollowersDetail> findByTaskQueueAndStatusByPage(TaskStatusEnum status, TaskQueue taskQueue, int page, int size);

}
