package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.bean.enumtype.LanguageEnum;
import org.example.bean.enumtype.TaskTypeEnum;
import org.example.entity.Followers;
import org.example.entity.TaskQueue;
import org.example.entity.TaskSendPromoteMessage;
import org.example.service.SeleniumService;
import org.example.utils.CrawlingUtil;
import org.example.utils.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${webdriver.chrome.path}")
    private String chromeDriverPath;

    @Override
    public void crawlFollowerDetailByCssStyle(Followers follower) {
        WebDriver driver = getDriver();
        try {
            searchForAccount(driver, follower.getFollowerUserName());
            List<WebElement> elementsSearchByStyle = captureDataByStyle(driver, follower);
            convertData(elementsSearchByStyle, follower);
            log.info("follower = {}", follower);
        } catch (Exception e) {
            log.error("SeleniumServiceImpl 爬取追蹤者明細時發生例外錯誤", e);
        } finally {
            driver.quit();
        }
    }

    @Override
    public void sendPromoteMessage(TaskSendPromoteMessage taskSendPromoteMessage, WebDriver driver) {
        LanguageEnum la = StringUtils.detectLanguage(taskSendPromoteMessage.getAccountFullName().trim());
        log.info("檢測到的姓名語言為：{}", la);
        String message = getLanguageText(la, taskSendPromoteMessage);
        try {
            //點擊新訊息
            clickNewMessage(driver);
            CrawlingUtil.pauseBetweenRequests(3, 5);
            //搜尋框輸入帳號
            WebElement searchBox = driver.findElement(By.name("queryBox"));
            searchBox.sendKeys(taskSendPromoteMessage.getAccount());
            CrawlingUtil.pauseBetweenRequests(5, 8);
            //比對並點擊目標使用者
            clickTargetUser(driver, taskSendPromoteMessage.getAccount());
            CrawlingUtil.pauseBetweenRequests(1, 2);
            //點擊聊天泡泡
            WebElement chatElement = driver.findElement(By.xpath("//div[@tabindex='0' and text()='聊天']"));
            chatElement.click();
            CrawlingUtil.pauseBetweenRequests(1, 2);
            //輸入訊息並發送
            textMessageAndSend(driver, message, taskSendPromoteMessage);
            CrawlingUtil.pauseBetweenRequests(3, 5);
        } catch (Exception e) {
            log.error("SeleniumServiceImpl 發送推廣訊息時發生例外錯誤", e);
        }
    }

    @Override
    public boolean isReadyForCrawl(TaskQueue taskQueue, WebDriver driver) {
        log.info("檢查Selenium是否準備好爬取");
        boolean isReadyOrNot = true;
        try {
            if (taskQueue.getTaskConfig().getTaskType().equals(TaskTypeEnum.GET_FOLLOWERS_DETAIL)) {
                List<WebElement> elementsWithStyle = driver.findElements(By.xpath("//h1[contains(@style, 'width: calc(100% - 100px);')]"));
                isReadyOrNot = elementsWithStyle.isEmpty();

            } else if (taskQueue.getTaskConfig().getTaskType().equals(TaskTypeEnum.SEND_PROMOTE_MESSAGE)) {
                List<WebElement> svgElements = driver.findElements(By.cssSelector("svg.x1lliihq.x1n2onr6.x5n08af"));
                for (WebElement svgElement : svgElements) {
                    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                    // 取得title
                    String titleText = (String) jsExecutor.executeScript("return arguments[0].getElementsByTagName('title')[0].textContent;", svgElement);
                    if ("新訊息".equals(titleText)) {
                        isReadyOrNot = false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("SeleniumServiceImpl 檢查是否處於預備爬蟲畫面時發生例外錯誤", e);
        }
        return isReadyOrNot;
    }

    @Override
    public WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }


    // private

    private void textMessageAndSend(WebDriver driver, String message, TaskSendPromoteMessage taskSendPromoteMessage) {
        //點擊訊息框
        WebElement elementsWithStyle = driver.findElement(By.xpath("//*[contains(@style, 'user-select: text; white-space: pre-wrap; word-break: break-word;')]"));
        elementsWithStyle.click();
        CrawlingUtil.pauseBetweenRequests(1, 2);
        //輸入訊息
        elementsWithStyle.sendKeys(taskSendPromoteMessage.getPostUrl());
        Actions actions = new Actions(driver);
        actions.sendKeys(elementsWithStyle, Keys.ENTER);
        actions.perform();
        CrawlingUtil.pauseBetweenRequests(1, 2);
        elementsWithStyle.sendKeys(message);
        actions.sendKeys(elementsWithStyle, Keys.ENTER);
        actions.perform();
    }

    /**
     * 點擊目標使用者
     */
    private void clickTargetUser(WebDriver driver, String account) {
        List<WebElement> elementsWithStyle = driver.findElements(By.xpath("//*[contains(@style, 'line-height: var(--base-line-clamp-line-height); --base-line-clamp-line-height: 18px;')]"));
        log.info("找到元素s：{}", elementsWithStyle);
        for (WebElement element : elementsWithStyle) {
            List<WebElement> spanElements = element.findElements(By.tagName("span"));
            for (WebElement span : spanElements) {
                if (account.trim().equals(span.getText().trim())) {
                    log.info("找到匹配的元素：{}", span.getText());
                    span.click();
                    return;
                }
            }
            log.error("未找到匹配的span元素");
        }
        log.error("未找到匹配的style元素");
    }


    /**
     * 點擊新訊息
     */
    private void clickNewMessage(WebDriver driver) {
        List<WebElement> svgElements = driver.findElements(By.cssSelector("svg.x1lliihq.x1n2onr6.x5n08af")
        );
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        for (WebElement svgElement : svgElements) {
            // 取得title
            String titleText = (String) jsExecutor.executeScript("return arguments[0].getElementsByTagName('title')[0].textContent;", svgElement);
            if ("新訊息".equals(titleText)) {
                svgElement.click();
                return;
            }
        }
        log.info("未找到新訊息元素");
    }


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
        WebElement searchInput = driver.findElement(By.xpath("//input[@aria-label='搜尋輸入']"));
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
        return driver.findElements(By.xpath("//*[contains(@style, 'line-height: var(--base-line-clamp-line-height); --base-line-clamp-line-height: 18px;')]"));
    }

    private String getLanguageText(LanguageEnum languageEnum, TaskSendPromoteMessage taskSendPromoteMessage) {
        return switch (languageEnum) {
            case ZH_TW -> taskSendPromoteMessage.getTextZhTw();
            case EN -> taskSendPromoteMessage.getTextEn();
            case JA -> taskSendPromoteMessage.getTextJa();
            case RU -> taskSendPromoteMessage.getTextRu();
            default -> taskSendPromoteMessage.getTextEn();
        };
    }
}
