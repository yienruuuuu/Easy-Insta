package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.dao.TaskQueueDao;
import org.example.entity.*;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.*;
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
@Slf4j
public class TaskQueueServiceImpl implements TaskQueueService {


    private final TaskQueueDao taskQueueDao;
    private final TaskConfigService taskConfigService;
    private final FollowersService followersService;
    private final MediaService mediaService;
    private final TaskQueueMediaService taskQueueMediaService;

    public TaskQueueServiceImpl(TaskQueueDao taskQueueDao, TaskConfigService taskConfigService, FollowersService followersService, MediaService mediaService, TaskQueueMediaService taskQueueMediaService) {
        this.taskQueueDao = taskQueueDao;
        this.taskConfigService = taskConfigService;
        this.followersService = followersService;
        this.mediaService = mediaService;
        this.taskQueueMediaService = taskQueueMediaService;
    }

    @Override
    public boolean checkTaskQueueExistByUserAndTaskType(IgUser targetUser, TaskTypeEnum taskType) {
        List<TaskQueue> taskQueues = taskQueueDao.findTaskQueuesByCustomQuery(taskType, targetUser, TaskStatusEnum.getUnfinishedStatus());
        return !taskQueues.isEmpty();
    }

    @Override
    @Transactional
    public TaskQueue createTaskQueueAndDeleteOldData(IgUser igUser, TaskTypeEnum taskType, TaskStatusEnum status) {
        deleteOldDataByTaskTypeAndIgUser(taskType, igUser);
        return saveTaskQueueAndTaskQueueDetail(igUser, taskType, status);
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

    /**
     * 創建並保存任務
     *
     * @param igUser   IG用戶
     * @param taskType 任務類型
     * @param status   任務狀態
     * @return 任務
     */
    private TaskQueue saveTaskQueueAndTaskQueueDetail(IgUser igUser, TaskTypeEnum taskType, TaskStatusEnum status) {
        TaskConfig taskConfig = taskConfigService.findByTaskType(taskType);
        TaskQueue newTask = TaskQueue.builder()
                .igUser(igUser)
                .taskConfig(taskConfig)
                .status(status)
                .submitTime(LocalDateTime.now())
                .build();
        Optional<TaskQueue> taskQueue = save(newTask);
        if (taskQueue.isEmpty()) {
            log.info("username: {}的 {} 任務建立失敗", igUser.getUserName(), taskType);
            throw new ApiException(SysCode.TASK_CREATION_FAILED);
        }
        //保存media任務需要先安排所有的media到task_queue_media表
        Integer saveSize = arrangeMediaToTaskQueueMedia(taskType, igUser, taskQueue.get());
        log.info("username: {}的 {} 任務建立成功, 保存的task_queue_media數量: {}", igUser.getUserName(), taskType, saveSize);
        return taskQueue.get();
    }

    /**
     * 根據任務類型和IG用戶刪除舊數據
     *
     * @param taskType 任務類型
     * @param igUser   IG用戶
     */
    private void deleteOldDataByTaskTypeAndIgUser(TaskTypeEnum taskType, IgUser igUser) {
        switch (taskType) {
            case GET_FOLLOWERS:
                followersService.deleteOldFollowersDataByIgUser(igUser);
                break;
            case GET_MEDIA:
                mediaService.deleteOldMediaDataByIgUserId(igUser.getId());
                break;
            case GET_MEDIA_COMMENT, GET_MEDIA_LIKER:
                break;
            default:
                throw new ApiException(SysCode.TASK_TYPE_NOT_FOUND);
        }
    }

    /**
     * media任務需要先安排所有的media到task_queue_media表
     *
     * @param taskType 任務類型
     * @param igUser   IG用戶
     */
    private Integer arrangeMediaToTaskQueueMedia(TaskTypeEnum taskType, IgUser igUser, TaskQueue taskQueue) {
        if (!TaskTypeEnum.GET_MEDIA_COMMENT.equals(taskType) && !TaskTypeEnum.GET_MEDIA_LIKER.equals(taskType)) {
            return 0;
        }
        int commentCount = taskType.equals(TaskTypeEnum.GET_MEDIA_COMMENT) ? 0 : -1;
        List<Media> medias = mediaService.listMediaByIgUserIdAndCommentCount(igUser, commentCount);
        List<TaskQueueMedia> taskQueueMedias = medias.stream()
                .map(media -> TaskQueueMedia.builder()
                        .media(media)
                        .taskQueueId(taskQueue)
                        .status(TaskStatusEnum.PENDING)
                        .build())
                .toList();
        return taskQueueMediaService.saveAll(taskQueueMedias).size();
    }
}
