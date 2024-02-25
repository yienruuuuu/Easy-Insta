package org.example.bean.enumtype;

import lombok.Getter;

/**
 * @author Eric.Lee
 * Date: 2024/2/17
 */
@Getter
public enum TaskTypeEnum {
    /**
     * 排程任务類型
     */
    GET_FOLLOWERS("獲取追隨者"),
    GET_POST("獲取貼文資料");

    private final String description;

    TaskTypeEnum(String description) {
        this.description = description;
    }
}
