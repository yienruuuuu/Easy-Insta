package org.example.service;

import org.example.entity.Followers;
import org.example.entity.TaskQueue;
import org.example.entity.TaskSendPromoteMessage;
import org.openqa.selenium.WebDriver;

/**
 * @author Eric.Lee
 * Date: 2024/3/12
 */
public interface SeleniumService {
    /**
     * 透過css選擇器找尋style以爬取追蹤者詳細資訊
     *
     * @param follower 追蹤者
     */
    void crawlFollowerDetailByCssStyle(Followers follower, WebDriver driver);

    /**
     * 透過css選擇器找尋style以爬取追蹤者詳細資訊
     *
     * @param taskSendPromoteMessage 任務發送推廣訊息
     */
    void sendPromoteMessage(TaskSendPromoteMessage taskSendPromoteMessage, WebDriver driver);

    /**
     * 透過分享影片來發布推廣訊息
     *
     * @param taskSendPromoteMessage 任務發送推廣訊息
     */
    void sendPromoteMessageByPostShare(TaskSendPromoteMessage taskSendPromoteMessage, WebDriver driver);

    /**
     * 移動到影片畫面已準備分享推廣訊息
     *
     * @param taskSendPromoteMessage 任務發送推廣訊息
     */
    String readyForPromoteMessageByPostShare(TaskSendPromoteMessage taskSendPromoteMessage, WebDriver driver);

    /**
     * 是否準備好爬取
     *
     * @param taskQueue 任務隊列
     * @return 是否準備好爬取
     */
    boolean isReadyForCrawl(TaskQueue taskQueue, WebDriver driver);

    /**
     * 取得WebDriver
     *
     * @return WebDriver
     */
    WebDriver getDriver();
}
