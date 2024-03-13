package org.example.dao;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueFollowersDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TaskQueueFollowersDetailDao extends JpaRepository<TaskQueueFollowersDetail, Integer> {

    /**
     * 透過狀態查詢任務隊列追蹤者明細
     *
     * @param status   狀態
     * @param pageable 分頁
     * @return 追蹤者明細列表
     */
    Page<TaskQueueFollowersDetail> findByStatusAndTaskQueue(@Param("status") TaskStatusEnum status, @Param("taskQueue") TaskQueue taskQueue, Pageable pageable);

}