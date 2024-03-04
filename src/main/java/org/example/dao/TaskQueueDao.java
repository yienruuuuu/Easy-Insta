package org.example.dao;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.IgUser;
import org.example.entity.TaskQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
public interface TaskQueueDao extends JpaRepository<TaskQueue, Integer> {
    /**
     * 依據任務類型、用戶ID、任務狀態查詢任務序列
     *
     * @param taskType 任務類型
     * @param igUser   對象用戶
     * @param statuses 任務狀態集合
     * @return 任務序列集合
     */
    @Query("SELECT t FROM TaskQueue t WHERE t.taskConfig.taskType = :taskType AND t.igUser = :igUser AND t.status IN :statuses ORDER BY t.submitTime DESC")
    List<TaskQueue> findTaskQueuesByCustomQuery(@Param("taskType") TaskTypeEnum taskType, @Param("igUser") IgUser igUser, @Param("statuses") List<TaskStatusEnum> statuses);

    /**
     * 檢查目前是否有某狀態及某登入需求的任務存在
     *
     * @param status 任務狀態
     * @param needLoginIg 是否需要登入 Instagram
     * @return 存在任務返回 true，否則返回 false
     */
    @Query("SELECT COUNT(t) > 0 FROM TaskQueue t WHERE t.status = :status AND t.taskConfig.needLoginIg = :needLoginIg")
    boolean existsInProgressTasks(@Param("status") TaskStatusEnum status, @Param("needLoginIg") boolean needLoginIg);

    /**
     * 依據任務狀態及提交時間排序，查詢最早提交的一筆任務序列
     *
     * @param status 任務狀態
     * @return 任務序列
     */
    Optional<TaskQueue> findFirstByStatusAndTaskConfig_NeedLoginIgOrderBySubmitTimeDesc(TaskStatusEnum status, boolean needLoginIg);


    /**
     * 依據任務狀態查詢任務序列
     *
     * @param status 任務狀態
     * @return 任務序列集合
     */
    List<TaskQueue> findTaskQueuesByStatus(TaskStatusEnum status);

    /**
     * 依據任務ID查詢任務序列
     *
     * @param taskId 任務ID
     * @return boolean
     */
    Optional<TaskQueue> findById(BigInteger taskId);
}
