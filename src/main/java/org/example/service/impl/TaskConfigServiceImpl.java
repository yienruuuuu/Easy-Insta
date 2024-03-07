package org.example.service.impl;

import org.example.bean.enumtype.TaskTypeEnum;
import org.example.dao.TaskConfigDao;
import org.example.entity.TaskConfig;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.TaskConfigService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/26
 */
@Service("taskConfigService")
public class TaskConfigServiceImpl implements TaskConfigService {
    private final TaskConfigDao taskConfigDao;

    public TaskConfigServiceImpl(TaskConfigDao taskConfigDao) {
        this.taskConfigDao = taskConfigDao;
    }

    @Override
    public Optional<TaskConfig> save(TaskConfig target) {
        return Optional.of(taskConfigDao.save(target));
    }

    @Override
    public Optional<TaskConfig> findById(Integer id) {
        return Optional.of(taskConfigDao.findById(BigInteger.valueOf(id)).get());
    }

    @Override
    public List<TaskConfig> findAll() {
        return taskConfigDao.findAll();
    }

    @Override
    public TaskConfig findByTaskType(TaskTypeEnum taskType) {
        return taskConfigDao.findByTaskType(taskType)
                .orElseThrow(() -> new ApiException(SysCode.TASK_CONFIG_NOT_FOUND));
    }
}
