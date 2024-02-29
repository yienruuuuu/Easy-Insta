package org.example.service;

import org.example.bean.enumtype.ConfigEnum;
import org.example.entity.Config;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
public interface ConfigService {
    Config getConfig(ConfigEnum param);

    List<Config> getConfigs();
}
