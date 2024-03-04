package org.example.bean.enumtype;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum TaskStatusEnum {
    /**
     * 排程任务状态
     */
    PENDING("等待中"),
    IN_PROGRESS("进行中"),
    PAUSED("暂停中"),
    COMPLETED("已完成"),
    FAILED("失败");

    private final String description;

    TaskStatusEnum(String description) {
        this.description = description;
    }

    /**
     * 檢查是否可以转换到目标状态
     *
     * @param targetStatus 目标状态
     * @return 是否可以转换
     */
    public boolean canTransitionTo(TaskStatusEnum targetStatus) {
        return switch (this) {
            case PENDING -> targetStatus == IN_PROGRESS;
            case IN_PROGRESS -> targetStatus == COMPLETED || targetStatus == FAILED;
            default -> false;
        };
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
