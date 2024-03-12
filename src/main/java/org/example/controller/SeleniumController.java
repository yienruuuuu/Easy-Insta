package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.CrawlingUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "selenium controller", description = "網頁爬蟲用API")
@RestController
@RequestMapping("selenium")
public class SeleniumController extends BaseController {

    @GetMapping("/trigger-selenium")
    public void triggerSeleniumOperation() {
        WebDriver driver = getDriver();
        try {
            String account = "brian_";

            // 滑鼠移動到某個輸入框並輸入帳號
            WebElement searchInput = driver.findElement(By.xpath("//input[@aria-label='搜尋輸入']"));
            searchInput.clear();

            Actions actions = new Actions(driver);
            actions.moveToElement(searchInput)
                    .click()
                    .sendKeys(account)
                    .build()
                    .perform();

            // 模擬等待動態結果
            CrawlingUtil.pauseBetweenRequests(1, 3);
            // 使用X Path根據文字內容定位使用者名稱元素
            WebElement userNameElement = driver.findElement(By.xpath("//span[text()='brian_']"));

            // 使用Actions類別來模擬滑鼠懸停操作
            Actions action = new Actions(driver);
            action.moveToElement(userNameElement).perform();

            // 等待彈跳窗出現並取得數據
            CrawlingUtil.pauseBetweenRequests(1, 3);


            List<WebElement> elementsWithStyle = driver.findElements(By.xpath("//*[contains(@style, 'line-height: var(--base-line-clamp-line-height); --base-line-clamp-line-height: 18px;')]"));

            for (int i = 0; i < elementsWithStyle.size(); i++) {
                // 例如，你可以基於元素的文字內容來決定是否是你想要的元素
                String text = elementsWithStyle.get(i).getText();
                System.out.println("找到元素: " + text);
                if (text.contains("特定標識符")) {
                    System.out.println("找到目标元素: " + text);
                    // 在找到目標元素後執行相關操作
                    break; // 如果已經找到，跳出循環
                }
            }
        } catch (Exception e) {
            log.error("Selenium操作异常", e);
        } finally {
            driver.quit(); // 操作完成後關閉Web Driver
        }
    }

    //private

    /**
     * 获取WebDriver
     */
    private WebDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "127.0.0.1:9222");
        return new ChromeDriver(options);
    }
}