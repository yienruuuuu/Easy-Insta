package org.example.task;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.service.InstagramService;
import org.example.service.LoginService;
import org.example.service.TaskQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
@Slf4j
@Service("checkTaskQueue")
public class CheckTaskQueue extends BaseQueue {
    @Autowired
    TaskQueueService taskQueueService;
    @Autowired
    InstagramService instagramService;
    @Autowired
    LoginService loginService;
    @Autowired
    TaskExecutionService taskExecutionService;

    @Scheduled(fixedDelayString = "${taskQueue.checkDelay:10000}")
    public void checkAndExecuteNeedLoginTasks() {
        log.info("檢查任務序列開始");
        if (!checkTaskEnabled()) return;
        if (isInProgressTaskExists()) return;

        processTasksByStatus(Arrays.asList(TaskStatusEnum.PAUSED, TaskStatusEnum.PENDING));
        log.info("檢查任務序列結束");
    }

    //private

    /**
     * 檢查是否有正在執行中且需要登入的任務
     *
     * @return 是否有正在執行中的任務
     */
    private boolean isInProgressTaskExists() {
        boolean exists = taskQueueService.checkTasksByStatusAndNeedLogin(TaskStatusEnum.IN_PROGRESS, true);
        if (exists) {
            log.info("有需要登入(NEED LOGIN IG)的任務正在執行中(IN_PROGRESS)，不執行新任務");
        }
        return exists;
    }

    /**
     * 根據任務狀態List處理任務
     *
     * @param statusList 任務狀態列表
     */
    private void processTasksByStatus(List<TaskStatusEnum> statusList) {
        for (TaskStatusEnum status : statusList) {
            Optional<TaskQueue> task = taskQueueService.findFirstTaskQueueByStatusAndNeedLogin(status, true);
            if (task.isEmpty()) {
                log.info("沒有需要登入且處於{}狀態的任務", status);
                continue;
            }
            log.info("發現需要登入且處於{}狀態的任務: {}", status, task.get());
            updateAndExecuteTask(task.get());
            break;
        }
    }

    /**
     * 更新任務狀態並執行任務
     *
     * @param taskQueue 任務
     */
    private void updateAndExecuteTask(TaskQueue taskQueue) {
        TaskQueue latestTaskQueue = taskQueueService.updateTaskStatus(taskQueue.getId(), TaskStatusEnum.IN_PROGRESS);
        taskExecutionService.executeGetFollowerTask(latestTaskQueue);
    }

}
