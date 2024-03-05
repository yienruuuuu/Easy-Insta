package org.example.service.impl;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.dao.TaskQueueDao;
import org.example.entity.IgUser;
import org.example.entity.TaskConfig;
import org.example.entity.TaskQueue;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.FollowersService;
import org.example.service.MediaService;
import org.example.service.TaskConfigService;
import org.example.service.TaskQueueService;
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


    private final TaskQueueDao taskQueueDao;
    private final TaskConfigService taskConfigService;
    private final FollowersService followersService;
    private final MediaService mediaService;

    public TaskQueueServiceImpl(TaskQueueDao taskQueueDao, TaskConfigService taskConfigService, FollowersService followersService, MediaService mediaService) {
        this.taskQueueDao = taskQueueDao;
        this.taskConfigService = taskConfigService;
        this.followersService = followersService;
        this.mediaService = mediaService;
    }

    @Override
    public boolean checkGetFollowersTaskQueueExist(IgUser targetUser, TaskTypeEnum taskType) {
        List<TaskQueue> taskQueues = taskQueueDao.findTaskQueuesByCustomQuery(taskType, targetUser, TaskStatusEnum.getUnfinishedStatus());
        return !taskQueues.isEmpty();
    }

    @Override
    @Transactional
    public Optional<TaskQueue> createTaskQueueAndDeleteOldData(IgUser igUser, TaskTypeEnum taskType, TaskStatusEnum status) {
        deleteOldDataByTaskTypeAndIgUser(taskType, igUser);
        return createAndSaveTaskQueue(igUser, taskType, status);
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


    //private

    private Optional<TaskQueue> createAndSaveTaskQueue(IgUser igUser, TaskTypeEnum taskType, TaskStatusEnum status) {
        TaskConfig taskConfig = taskConfigService.findByTaskType(taskType);
        TaskQueue newTask = TaskQueue.builder()
                .igUser(igUser)
                .taskConfig(taskConfig)
                .status(status)
                .submitTime(LocalDateTime.now())
                .build();
        return save(newTask);
    }

    private void deleteOldDataByTaskTypeAndIgUser(TaskTypeEnum taskType, IgUser igUser) {
        switch (taskType) {
            case GET_FOLLOWERS:
                followersService.deleteOldFollowersDataByIgUser(igUser);
                break;
            case GET_MEDIA:
                mediaService.deleteOldMediaDataByIgUserId(igUser.getId());
                break;
            default:
                throw new ApiException(SysCode.TASK_TYPE_NOT_FOUND);
        }
    }
}
