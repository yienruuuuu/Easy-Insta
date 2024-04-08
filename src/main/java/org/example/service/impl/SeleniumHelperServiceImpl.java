package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.TaskQueue;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.SeleniumHelperService;
import org.example.service.SeleniumService;
import org.example.utils.CrawlingUtil;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

/**
 * @author Eric.Lee
 * Date: 2024/4/9
 */
@Service("seleniumHelperService")
@Slf4j
public class SeleniumHelperServiceImpl implements SeleniumHelperService {
    private static final long TIME_OUT = 60000;
    private final SeleniumService seleniumService;

    public SeleniumHelperServiceImpl(SeleniumService seleniumService) {
        this.seleniumService = seleniumService;
    }

    @Override
    public void waitForSeleniumReady(TaskQueue taskQueue, WebDriver driver) {
        long startTime = System.currentTimeMillis();
        while (seleniumService.isReadyForCrawl(taskQueue, driver)) {
            log.info("等待Selenium準備完成");
            if (System.currentTimeMillis() - startTime > TIME_OUT) {
                throw new ApiException(SysCode.TASK_WAIT_SELENIUM_TIME_OUT);
            }
            CrawlingUtil.pauseBetweenRequests(10, 10);
        }
        log.info("Selenium已準備完成");

    }
}
