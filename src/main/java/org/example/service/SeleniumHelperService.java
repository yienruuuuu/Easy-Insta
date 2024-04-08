package org.example.service;

import org.example.entity.TaskQueue;
import org.openqa.selenium.WebDriver;

/**
 * @author Eric.Lee
 * Date: 2024/4/9
 */
public interface SeleniumHelperService {
    void waitForSeleniumReady(TaskQueue taskQueue, WebDriver driver);
}
