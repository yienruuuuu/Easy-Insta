package org.example.bean.enumtype;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum InitStatusEnum {
    /**
     * 任務初始狀態
     */
    DAILY_PENDING("等待每日任務執行"),
    PENDING("等待中"),;

    private final String description;

    InitStatusEnum(String description) {
        this.description = description;
    }
}
