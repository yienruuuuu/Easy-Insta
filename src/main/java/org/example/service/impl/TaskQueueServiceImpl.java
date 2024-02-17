package org.example.service.impl;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.dao.TaskQueueDao;
import org.example.entity.TaskQueue;
import org.example.service.TaskQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
@Service("taskQueueService")
public class TaskQueueServiceImpl implements TaskQueueService {

    @Autowired
    TaskQueueDao taskQueueDao;

    @Override
    public boolean checkGetFollowersTaskQueueExist(String userId, TaskTypeEnum taskType) {
        List<TaskQueue> taskQueues = taskQueueDao.findTaskQueuesByCustomQuery(taskType, userId, TaskStatusEnum.getUnfinishedStatus());
        return !taskQueues.isEmpty();
    }

    @Override
    public Optional<TaskQueue> createAndSaveTaskQueue(String username, TaskTypeEnum taskType, TaskStatusEnum status) {
        TaskQueue newTask = TaskQueue.builder()
                .userId(username)
                .taskType(taskType)
                .status(status)
                .submitTime(LocalDateTime.now())
                .build();
        return save(newTask);
    }

    @Override
    public Optional<TaskQueue> save(TaskQueue target) {
        return Optional.of(taskQueueDao.save(target));
    }

    @Override
    public Optional<TaskQueue> findById(Integer id) {
        return taskQueueDao.findById(id);
    }

    @Override
    public List<TaskQueue> findAll() {
        return taskQueueDao.findAll();
    }

}
