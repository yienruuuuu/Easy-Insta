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
    private final TaskQueueFollowerDetailService taskQueueFollowerDetailService;

    public TaskQueueServiceImpl(TaskQueueDao taskQueueDao, TaskConfigService taskConfigService, FollowersService followersService, MediaService mediaService, TaskQueueMediaService taskQueueMediaService, TaskQueueFollowerDetailService taskQueueFollowerDetailService) {
        this.taskQueueDao = taskQueueDao;
        this.taskConfigService = taskConfigService;
        this.followersService = followersService;
        this.mediaService = mediaService;
        this.taskQueueMediaService = taskQueueMediaService;
        this.taskQueueFollowerDetailService = taskQueueFollowerDetailService;
    }

    @Override
    public boolean checkTaskQueueExistByUserAndTaskType(IgUser targetUser, TaskTypeEnum taskType) {
        List<TaskQueue> taskQueues = taskQueueDao.findTaskQueuesByCustomQuery(taskType, targetUser, TaskStatusEnum.getUnfinishedStatus());
        return !taskQueues.isEmpty();
    }

    @Override
    @Transactional
    public TaskQueue createTaskQueueAndDeleteOldData(IgUser igUser, TaskTypeEnum taskType) {
        deleteOldDataByTaskTypeAndIgUser(taskType, igUser);
        return saveTaskQueueAndTaskQueueDetail(igUser, taskType);
    }

    @Override
    public boolean checkTasksByStatusAndNeedLogin(List<TaskStatusEnum> status, boolean needLoginIg) {
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


    @Override
    public List<TaskQueue> saveAll(List<TaskQueue> tasks) {
        return taskQueueDao.saveAll(tasks);
    }

    //private

    /**
     * 創建並保存任務
     *
     * @param igUser   IG用戶
     * @param taskType 任務類型
     * @return 任務
     */
    private TaskQueue saveTaskQueueAndTaskQueueDetail(IgUser igUser, TaskTypeEnum taskType) {
        TaskConfig taskConfig = taskConfigService.findByTaskType(taskType);
        TaskQueue newTask = TaskQueue.builder()
                .igUser(igUser)
                .taskConfig(taskConfig)
                .status(taskConfig.mapInitStatusToTaskStatus(taskConfig.getInitStatus()))
                .submitTime(LocalDateTime.now())
                .build();
        Optional<TaskQueue> taskQueue = save(newTask);
        if (taskQueue.isEmpty()) {
            log.info("username: {}的 {} 任務建立失敗", igUser.getUserName(), taskType);
            throw new ApiException(SysCode.TASK_CREATION_FAILED);
        }
        //保存任務明細到任務明細表
        arrangeToTaskQueueDetail(taskType, igUser, taskQueue.get());
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
            case GET_MEDIA_COMMENT, GET_MEDIA_LIKER, GET_FOLLOWERS_DETAIL, SEND_PROMOTE_MESSAGE,
                 SEND_PROMOTE_MESSAGE_BY_POST_SHARE:
                break;
            default:
                throw new ApiException(SysCode.TASK_TYPE_NOT_FOUND);
        }
    }

    /**
     * 根據任務類型新增detail任務
     *
     * @param taskType 任務類型
     * @param igUser   IG用戶
     */
    private void arrangeToTaskQueueDetail(TaskTypeEnum taskType, IgUser igUser, TaskQueue taskQueue) {
        switch (taskType) {
            case GET_MEDIA_COMMENT, GET_MEDIA_LIKER:
                arrangeMediaToTaskQueueMedia(taskType, igUser, taskQueue);
                break;
            case GET_FOLLOWERS_DETAIL:
                arrangeToTaskQueueFollowerDetail(igUser, taskQueue);
                break;
            default:
                break;
        }
    }

    /**
     * media任務需要先安排所有的media到task_queue_media表
     *
     * @param taskType 任務類型
     * @param igUser   IG用戶
     */
    private void arrangeMediaToTaskQueueMedia(TaskTypeEnum taskType, IgUser igUser, TaskQueue taskQueue) {
        int commentCount = taskType.equals(TaskTypeEnum.GET_MEDIA_COMMENT) ? 0 : -1;
        List<Media> medias = mediaService.listMediaByIgUserIdAndCommentCount(igUser, commentCount);
        List<TaskQueueMedia> taskQueueMedias = medias.stream()
                .map(media -> TaskQueueMedia.builder()
                        .media(media)
                        .taskQueue(taskQueue)
                        .status(TaskStatusEnum.PENDING)
                        .build())
                .toList();
        taskQueueMediaService.saveAll(taskQueueMedias);
    }

    /**
     * follower_detail任務需要先安排所有的Follower到task_queue_Follower_detail表
     *
     * @param igUser IG用戶
     */
    private void arrangeToTaskQueueFollowerDetail(IgUser igUser, TaskQueue taskQueue) {
        List<Followers> followerList = followersService.findByIgUser(igUser);
        List<TaskQueueFollowersDetail> taskQueueFollowersDetails = followerList.stream().map(follower -> {
            TaskQueueFollowersDetail detail = new TaskQueueFollowersDetail();
            detail.setFollower(follower);
            detail.setTaskQueue(taskQueue);
            detail.setStatus(TaskStatusEnum.PENDING);
            return detail;
        }).toList();
        taskQueueFollowerDetailService.saveAll(taskQueueFollowersDetails);
    }
}
