package org.example.service.impl;

import org.example.bean.enumtype.ConfigEnum;
import org.example.dao.ConfigDao;
import org.example.entity.Config;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.ConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
@Service
public class ConfigServiceImpl implements ConfigService {
    private final ConfigDao configDao;

    public ConfigServiceImpl(ConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public Config getConfig(ConfigEnum param) {
        return configDao.findByParam(param.name()).orElseThrow(() -> new ApiException(SysCode.CONFIG_NOT_FOUND));
    }

    @Override
    public List<Config> getConfigs() {
        return configDao.findAll();
    }
}
