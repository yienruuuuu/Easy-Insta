package org.example.dao;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.TaskQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
public interface TaskQueueDao extends JpaRepository<TaskQueue, Integer> {
    /**
     * 依據任務類型、用戶ID、任務狀態查詢任務序列
     *
     * @param taskType 任務類型
     * @param userId   對象用戶Id
     * @param statuses 任務狀態集合
     * @return 任務序列集合
     */
    @Query("SELECT t FROM TaskQueue t WHERE t.taskType = :taskType AND t.userId = :userId AND t.status IN :statuses ORDER BY t.submitTime DESC")
    List<TaskQueue> findTaskQueuesByCustomQuery(@Param("taskType") TaskTypeEnum taskType, @Param("userId") String userId, @Param("statuses") List<TaskStatusEnum> statuses);
}
