package org.example;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebScraperTest implements CommandLineRunner {

    @Value("${webdriver.chrome.path}")
    private String chromeDriverPath;

    @Override
    public void run(String... args) throws Exception {
        //若瀏覽器安裝位置為預設則webDriver會自動搜尋path設定的位置，也可以使用System.setProperty 來指定路徑
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        //設定ChromeOptions，允許跨域
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // 創建並啟動BrowserMob Proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        // 0 表示選擇一個隨機可用的端口
        proxy.start(0);
        // 獲取代理服務器的Selenium代理對象
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        // 配置Selenium以使用代理
        options.setProxy(seleniumProxy);
        //Selenium對不同瀏覽器提供了不同的webDriver
        WebDriver driver = new ChromeDriver(options);

        // 設置響應攔截器
        proxy.addResponseFilter((response, contents, messageInfo) -> {
            // 到Google首頁
            driver.get("https://www.google.com.tw/");
            // 取得pageTitle
            String title = driver.getTitle();
            System.out.print(title);
        });

    }

    public static void main(String[] args) {
        SpringApplication.run(WebScraperTest.class, args);
    }
}
