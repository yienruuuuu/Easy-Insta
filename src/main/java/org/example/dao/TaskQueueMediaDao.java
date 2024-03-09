package org.example.dao;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskQueueMediaDao extends JpaRepository<TaskQueueMedia, Integer> {
    Optional<TaskQueueMedia> findFirstByTaskQueueIdAndStatus(TaskQueue taskQueue, TaskStatusEnum status);
}