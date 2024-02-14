package org.example.bean.enumtype;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {
    /**
     * 排程任务状态
     */
    PENDING("等待中"),
    IN_PROGRESS("进行中"),
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
}
