package org.example.bean.enumtype;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum TaskStatusEnum {
    /**
     * 排程任務狀態
     */
    PENDING("等待中"),
    IN_PROGRESS("进行中"),
    PAUSED("暂停中"),
    COMPLETED("已完成"),
    FAILED("失败"),
    DAILY_PENDING("等待每日任務執行"),
    DAILY_PAUSED("每日任務達上限但未完成"),
    DAILY_COMPLETED("每日任務已終結");

    private final String description;

    TaskStatusEnum(String description) {
        this.description = description;
    }

    /**
     * 获取未完成等級的任务状态
     *
     * @return 未完成狀態的列表
     */
    public static List<TaskStatusEnum> getUnfinishedStatus() {
        return Arrays.stream(TaskStatusEnum.values())
                .filter(status -> status == PENDING || status == IN_PROGRESS || status == PAUSED)
                .collect(Collectors.toList());
    }
}
