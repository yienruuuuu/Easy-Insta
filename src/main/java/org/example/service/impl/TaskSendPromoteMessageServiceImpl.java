package org.example.service.impl;

import org.example.bean.enumtype.TaskStatusEnum;
import org.example.dao.TaskSendPromoteMessageDao;
import org.example.entity.TaskQueue;
import org.example.entity.TaskSendPromoteMessage;
import org.example.service.TaskSendPromoteMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/4/8
 */
@Service("taskSendPromoteMessageService")
public class TaskSendPromoteMessageServiceImpl implements TaskSendPromoteMessageService {
    private final TaskSendPromoteMessageDao taskSendPromoteMessageDao;

    public TaskSendPromoteMessageServiceImpl(TaskSendPromoteMessageDao taskSendPromoteMessageDao) {
        this.taskSendPromoteMessageDao = taskSendPromoteMessageDao;
    }

    @Override
    public void saveAll(List<TaskSendPromoteMessage> taskSendPromoteMessageList) {
        taskSendPromoteMessageDao.saveAll(taskSendPromoteMessageList);
    }

    @Override
    public List<TaskSendPromoteMessage> findByTaskQueueAndStatus(TaskQueue taskQueue, TaskStatusEnum status) {
        return taskSendPromoteMessageDao.findByTaskQueueAndStatus(taskQueue, status);
    }
}
