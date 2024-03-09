package org.example.service.impl;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.dao.TaskQueueMediaDao;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueMedia;
import org.example.service.TaskQueueMediaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/3/8
 */
@Service("taskQueueMediaService")
public class TaskQueueMediaServiceImpl implements TaskQueueMediaService {
    private final TaskQueueMediaDao taskQueueMediaDao;

    public TaskQueueMediaServiceImpl(TaskQueueMediaDao taskQueueMediaDao) {
        this.taskQueueMediaDao = taskQueueMediaDao;
    }

    @Override
    public Optional<TaskQueueMedia> save(TaskQueueMedia target) {
        return Optional.of(taskQueueMediaDao.save(target));
    }

    @Override
    public Optional<TaskQueueMedia> findById(Integer id) {
        return taskQueueMediaDao.findById(id);
    }

    @Override
    public List<TaskQueueMedia> findAll() {
        return taskQueueMediaDao.findAll();
    }

    @Override
    public List<TaskQueueMedia> saveAll(List<TaskQueueMedia> taskQueueList) {
        return taskQueueMediaDao.saveAll(taskQueueList);
    }

    @Override
    public Optional<TaskQueueMedia> findOneByTaskQueue(TaskQueue taskQueue, TaskStatusEnum status) {
        return taskQueueMediaDao.findFirstByTaskQueueIdAndStatus(taskQueue, status);
    }
}
