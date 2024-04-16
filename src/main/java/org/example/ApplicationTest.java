package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.SeleniumService;
import org.example.utils.CrawlingUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"org.example.*"})
@EnableTransactionManagement
@EnableScheduling
@EnableCaching
@EnableAspectJAutoProxy
@EnableAsync
@Slf4j
public class ApplicationTest implements CommandLineRunner {
    private static final String FIRST_POST_DIV = "//div[contains(@style, 'display: flex; flex-direction: column; padding-bottom: 0px; padding-top: 0px; position: relative;')]";
    private static final String SVG_SHARE_ELEMENT = "svg.x1lliihq.x1n2onr6.x5n08af";
    private static final String JS_FUNCTIONT = "return arguments[0].getElementsByTagName('title')[0].textContent;";
    private static final String SVG_ELEMENT = "svg.x1lliihq.x1n2onr6.x5n08af";

    private final SeleniumService seleniumService;

    public ApplicationTest(SeleniumService seleniumService) {
        this.seleniumService = seleniumService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApplicationTest.class, args);
    }

    @Override
    public void run(String... args) {
        String targetA = "yienruuuuu";
        WebDriver driver = seleniumService.getDriver();
        String url = "https://www.instagram.com/tomato_yuki_/";
        driver.get(url);
        CrawlingUtil.pauseBetweenRequests(3, 5);
        WebElement elementsWithStyle = driver.findElements(By.xpath(FIRST_POST_DIV)).get(0);
        WebElement firstLink = elementsWithStyle.findElement(By.cssSelector("a"));
        log.info("Link: {}", firstLink.getAttribute("href"));
        //移動到最新影片
        driver.get(firstLink.getAttribute("href"));
        CrawlingUtil.pauseBetweenRequests(3, 5);
        //點擊分享貼文
        getFirstPost(driver);
        //搜尋
        WebElement searchBox = driver.findElement(By.name("queryBox"));
        searchBox.sendKeys(targetA);
        CrawlingUtil.pauseBetweenRequests(5, 8);
        //點擊目標使用者
        clickTargetUser(driver, targetA);
        CrawlingUtil.pauseBetweenRequests(5, 8);
        //輸入訊息
        textMessageAndSend(driver, "Hello");
        CrawlingUtil.pauseBetweenRequests(2, 2);
        //點擊傳送
        WebElement sendButton = driver.findElement(By.xpath("//div[text()='傳送']"));
        sendButton.click();
        CrawlingUtil.pauseBetweenRequests(2, 2);
    }

    /**
     * 點擊分享貼文
     *
     * @param driver WebDriver
     */
    private void getFirstPost(WebDriver driver) {
        List<WebElement> svgElements = driver.findElements(By.cssSelector(SVG_SHARE_ELEMENT));
        for (WebElement svgElement : svgElements) {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            // 取得title
            String titleText = (String) jsExecutor.executeScript(JS_FUNCTIONT, svgElement);
            if ("分享貼文".equals(titleText)) {
                svgElement.click();
                return;
            }
        }
    }

    /**
     * 點擊目標使用者
     */
    private void clickTargetUser(WebDriver driver, String account) {
        List<WebElement> elementsWithStyle = driver.findElements(By.xpath("//*[contains(@style, 'line-height: var(--base-line-clamp-line-height); --base-line-clamp-line-height: 18px;')]"));
        for (WebElement element : elementsWithStyle) {
            List<WebElement> spanElements = element.findElements(By.tagName("span"));
            for (WebElement span : spanElements) {
                if (account.trim().equals(span.getText().trim())) {
                    log.info("找到匹配的元素：{}", span.getText());
                    span.click();
                    return;
                }
            }
            log.warn("未找到匹配的span元素");
        }
        clickClose(driver);
        log.warn("未找到匹配的style元素");
    }

    /**
     * 關閉浮窗
     */
    private void clickClose(WebDriver driver) {
        List<WebElement> svgElements = driver.findElements(By.cssSelector(SVG_ELEMENT)
        );
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        for (WebElement svgElement : svgElements) {
            // 取得title
            String titleText = (String) jsExecutor.executeScript(JS_FUNCTIONT, svgElement);
            if ("關閉".equals(titleText)) {
                log.info("找到關閉元素");
                svgElement.click();
                return;
            }
        }
        log.warn("未找到關閉元素");
        throw new ApiException(SysCode.CLOSING_ELEMENT_NOT_FOUND);
    }

    private void textMessageAndSend(WebDriver driver, String message) {
        //點擊訊息框
        WebElement elementsWithStyle = driver.findElement(By.xpath("//input[@placeholder='留個話……']"));
        elementsWithStyle.click();
        CrawlingUtil.pauseBetweenRequests(1, 2);
        //輸入訊息
        elementsWithStyle.sendKeys(message);
    }
}