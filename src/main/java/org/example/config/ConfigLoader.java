package org.example.config;

import org.example.entity.Config;
import org.example.service.ConfigService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/2/29
 */
@Component
public class ConfigLoader {


    private final ConfigService configService;
    private final ConfigCache configCache;

    public ConfigLoader(ConfigService configService, ConfigCache configCache) {
        this.configService = configService;
        this.configCache = configCache;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadConfigDataToCache() {
        List<Config> allConfigs = configService.getConfigs();
        for (Config config : allConfigs) {
            configCache.put(config.getParam(), config.getValue());
        }
    }
}