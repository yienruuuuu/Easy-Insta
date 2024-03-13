package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.ConfigEnum;
import org.example.config.ConfigCache;
import org.example.entity.Followers;
import org.example.service.SeleniumService;
import org.example.utils.CrawlingUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/12
 */
@Service("seleniumService")
@Slf4j
public class SeleniumServiceImpl implements SeleniumService {
    public static final String POST_STRING = "則貼文";
    public static final String FOLLOWING_STRING = "粉絲";
    public static final String FOLLOWER_STRING = "人追蹤中";

    public SeleniumServiceImpl(ConfigCache configCache) {
        this.configCache = configCache;
    }

    private final ConfigCache configCache;


    @Override
    public Followers crawlFollowerDetailByCssStyle(Followers follower) {
        WebDriver driver = getDriver();
        try {
            searchForAccount(driver, follower.getFollowerUserName());
            List<WebElement> elementsSearchByStyle = captureDataByStyle(driver, follower);
            convertData(elementsSearchByStyle, follower);
            log.info("follower = {}", follower);
        } catch (Exception e) {
            log.error("Selenium操作異常", e);
        } finally {
            driver.quit();
        }
        return follower;
    }

    @Override
    public boolean isReadyForCrawl() {
        WebDriver driver = getDriver();
        List<WebElement> elementsWithStyle = driver.findElements(By.xpath(configCache.get(ConfigEnum.SELENIUM_IG_VIEW_FANS_SEARCH_STYLE.name())));
        return !elementsWithStyle.isEmpty();
    }

    // private

    /**
     * 將元素轉換為對象，並設置到實體中
     */
    private void convertData(List<WebElement> elementsSearchByStyle, Followers follower) {
        for (int i = 0; i < elementsSearchByStyle.size(); i++) {
            String text = elementsSearchByStyle.get(i).getText();
            log.info("找到元素: " + text);
            if (POST_STRING.contains(text)) {
                follower.setPostCount(Integer.parseInt(elementsSearchByStyle.get(i - 1).getText()));
            } else if (FOLLOWING_STRING.contains(text)) {
                follower.setFollowingCount(Integer.parseInt(elementsSearchByStyle.get(i - 1).getText()));
            } else if (FOLLOWER_STRING.contains(text)) {
                follower.setFollowerCount(Integer.parseInt(elementsSearchByStyle.get(i - 1).getText()));
            }
        }
    }


    /**
     * 搜尋框輸入帳號
     */
    private void searchForAccount(WebDriver driver, String account) {
        WebElement searchInput = driver.findElement(By.xpath(configCache.get(ConfigEnum.SELENIUM_IG_INPUT_STYLE.name())));
        searchInput.clear();
        Actions actions = new Actions(driver);
        actions.moveToElement(searchInput)
                .click()
                .sendKeys(account)
                .build()
                .perform();
        CrawlingUtil.pauseBetweenRequests(3, 5);
    }


    private List<WebElement> captureDataByStyle(WebDriver driver, Followers follower) {
        // 使用者名稱元素定位
        WebElement userNameElement = driver.findElement(By.xpath("//span[text()='" + follower.getFollowerUserName() + "']"));
        //滑鼠懸停
        Actions action = new Actions(driver);
        action.moveToElement(userNameElement).perform();
        // 等待個人頁面加載
        CrawlingUtil.pauseBetweenRequests(1, 3);
        // 定位粉絲數、追蹤數、貼文數的元素
        return driver.findElements(By.xpath(configCache.get(ConfigEnum.SELENIUM_IG_FOLLOWERS_DATA_STYLE.name())));
    }

    /**
     * 获取WebDriver
     */
    private WebDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        return new ChromeDriver(options);
    }
}
