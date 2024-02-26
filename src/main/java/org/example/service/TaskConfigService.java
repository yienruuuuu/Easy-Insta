package org.example.service;

import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.TaskConfig;

import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/26
 */
public interface TaskConfigService extends BaseService<TaskConfig>{
    /**
     * 依據任務類型查詢任務設定
     *
     * @param taskType 任務類型
     * @return 任務設定
     */
    TaskConfig findByTaskType(TaskTypeEnum taskType);
}
