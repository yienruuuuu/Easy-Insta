package org.example.service.impl;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.dao.TaskQueueFollowersDetailDao;
import org.example.entity.TaskQueue;
import org.example.entity.TaskQueueFollowersDetail;
import org.example.service.TaskQueueFollowerDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/3/13
 */
@Service("taskQueueFollowerDetailService")
public class TaskQueueFollowerDetailServiceImpl implements TaskQueueFollowerDetailService {
    private final TaskQueueFollowersDetailDao taskQueueFollowersDetailDao;

    public TaskQueueFollowerDetailServiceImpl(TaskQueueFollowersDetailDao taskQueueFollowersDetailDao) {
        this.taskQueueFollowersDetailDao = taskQueueFollowersDetailDao;
    }

    @Override
    public Optional<TaskQueueFollowersDetail> save(TaskQueueFollowersDetail target) {
        return Optional.of(taskQueueFollowersDetailDao.save(target));
    }

    @Override
    public Optional<TaskQueueFollowersDetail> findById(Integer id) {
        return taskQueueFollowersDetailDao.findById(id);
    }

    @Override
    public List<TaskQueueFollowersDetail> findAll() {
        return taskQueueFollowersDetailDao.findAll();
    }

    @Override
    public void saveAll(List<TaskQueueFollowersDetail> taskQueueList) {
        taskQueueFollowersDetailDao.saveAll(taskQueueList);
    }

    @Override
    public Page<TaskQueueFollowersDetail> findByTaskQueueAndStatusByPage(TaskStatusEnum status, TaskQueue taskQueue, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskQueueFollowersDetailDao.findByStatusAndTaskQueue(status, taskQueue, pageable);
    }
}
