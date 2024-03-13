package org.example.strategy;

import org.example.bean.enumtype.TaskTypeEnum;
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

    public TaskExecutionStrategyFactory(List<TaskStrategy> strategyList) {
        strategyList.forEach(strategy -> {
            if (strategy instanceof GetFollowerStrategy) {
                strategies.put(TaskTypeEnum.GET_FOLLOWERS, strategy);
            }
            if (strategy instanceof GetMediaStrategy) {
                strategies.put(TaskTypeEnum.GET_MEDIA, strategy);
            }
            if (strategy instanceof GetMediaCommentStrategy) {
                strategies.put(TaskTypeEnum.GET_MEDIA_COMMENT, strategy);
            }
            if (strategy instanceof GetMediaLikerStrategy) {
                strategies.put(TaskTypeEnum.GET_MEDIA_LIKER, strategy);
            }
            if (strategy instanceof GetFollowerDetailStrategy) {
                strategies.put(TaskTypeEnum.GET_FOLLOWERS_DETAIL, strategy);
            }
        });
    }

    public TaskStrategy getStrategy(TaskTypeEnum taskType) {
        return Optional.ofNullable(strategies.get(taskType))
                .orElseThrow(() -> new IllegalArgumentException("無效的任務類型 : " + taskType));
    }
}