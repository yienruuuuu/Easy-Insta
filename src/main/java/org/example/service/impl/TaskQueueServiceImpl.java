package org.example.service.impl;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.dao.TaskQueueDao;
import org.example.entity.TaskConfig;
import org.example.entity.TaskQueue;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.TaskConfigService;
import org.example.service.TaskQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
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
    @Autowired
    TaskConfigService taskConfigService;

    @Override
    public boolean checkGetFollowersTaskQueueExist(String userId, TaskTypeEnum taskType) {
        List<TaskQueue> taskQueues = taskQueueDao.findTaskQueuesByCustomQuery(taskType, userId, TaskStatusEnum.getUnfinishedStatus());
        return !taskQueues.isEmpty();
    }

    @Override
    public Optional<TaskQueue> createAndSaveTaskQueue(String username, TaskTypeEnum taskType, TaskStatusEnum status) {
        TaskConfig taskConfig = taskConfigService.findByTaskType(taskType);
        TaskQueue newTask = TaskQueue.builder()
                .userName(username)
                .taskConfig(taskConfig)
                .status(status)
                .submitTime(LocalDateTime.now())
                .build();
        return save(newTask);
    }

    @Override
    public boolean checkTasksByStatusAndNeedLogin(TaskStatusEnum status, boolean needLoginIg) {
        return taskQueueDao.existsInProgressTasks(status, needLoginIg);
    }

    @Override
    public Optional<TaskQueue> findFirstTaskQueueByStatusAndNeedLogin(TaskStatusEnum status, boolean needLoginIg) {
        return taskQueueDao.findFirstByStatusAndTaskConfig_NeedLoginIgOrderBySubmitTimeDesc(status, needLoginIg);
    }

    @Override
    public List<TaskQueue> findTasksByStatus(TaskStatusEnum status) {
        return taskQueueDao.findTaskQueuesByStatus(status);
    }

    @Transactional
    @Override
    public TaskQueue updateTaskStatus(BigInteger taskId, TaskStatusEnum newStatus) {
        Optional<TaskQueue> taskQueueOptional = taskQueueDao.findById(taskId);
        if (taskQueueOptional.isPresent()) {
            TaskQueue taskQueue = taskQueueOptional.get();
            taskQueue.setStatus(newStatus);
            return taskQueueDao.save(taskQueue);
        }
        throw new ApiException(SysCode.TASK_STATUS_UPDATE_FAILED);
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
