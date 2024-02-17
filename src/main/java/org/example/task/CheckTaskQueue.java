package org.example.task;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.TaskStatusEnum;
import org.example.entity.TaskQueue;
import org.example.service.TaskQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
@Slf4j
@Service("checkTaskQueue")
public class CheckTaskQueue {
    @Autowired
    TaskQueueService taskQueueService;

    @Scheduled(fixedDelay = 10000) // 例如，每10秒檢查一次
    public void checkAndExecuteNeedLoginTasks() {
        log.info("檢查任務序列開始，checkTaskQueue.checkAndExecuteTasks()運行中");
        // 如果有任務正在執行中，則不執行新任務
        if (taskQueueService.existsInProgressTasks(TaskStatusEnum.IN_PROGRESS)) {
            log.info("checkAndExecuteTasks運行中，有任務正在執行中(IN_PROGRESS)，不執行新任務");
            return;
        }
        // 查詢待處理的任務
        Optional<TaskQueue> pendingTasks = taskQueueService.findFirstTaskQueueByStatus(TaskStatusEnum.PENDING);
        if (pendingTasks.isEmpty()) {
            log.info("checkAndExecuteTasks運行中，沒有待處理的任務");
            return;
        }
        log.info("checkAndExecuteTasks運行中，有待處理的任務，任務: {}", pendingTasks.get());
//        for (TaskQueue task : pendingTasks) {
//            executeTask(task);
//        }
    }
}
