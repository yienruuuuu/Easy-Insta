package org.example.dao;

import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.TaskConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/26
 */
public interface TaskConfigDao extends JpaRepository<TaskConfig, BigInteger> {
    /**
     * 依據任務類型查詢任務設定
     *
     * @param taskType 任務類型
     * @return 任務設定
     */
    Optional<TaskConfig> findByTaskType(TaskTypeEnum taskType);

}
