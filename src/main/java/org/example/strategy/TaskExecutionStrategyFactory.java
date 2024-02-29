package org.example.strategy;

import org.example.bean.enumtype.TaskTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
@Component
public class TaskExecutionStrategyFactory {
    private final Map<TaskTypeEnum, TaskStrategy> strategies = new HashMap<>();

    @Autowired
    public TaskExecutionStrategyFactory(List<TaskStrategy> strategyList) {
        strategyList.forEach(strategy -> {
            if (strategy instanceof GetFollowerStrategy) {
                strategies.put(TaskTypeEnum.GET_FOLLOWERS, strategy);
            }
        });
    }
    public TaskStrategy getStrategy(TaskTypeEnum taskType) {
        return Optional.ofNullable(strategies.get(taskType))
                .orElseThrow(() -> new IllegalArgumentException("無效的任務類型 : " + taskType));
    }
}