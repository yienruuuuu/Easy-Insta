package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.ApiException;
import org.example.exception.SysCode;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Eric.Lee
 * Date: 2024/2/23
 */
@Slf4j
public final class CrawlingUtil {
    private CrawlingUtil() {
        // 拋出異常是為了防止透過反射呼叫私有建構函數
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 檢查已爬取的追蹤者数量是否達到目標比率。
     *
     * @param amountFromCrawler 已爬取的追蹤者数量
     * @param actualAmount      实际的追蹤者数量
     * @param rate              目標比率
     * @return 如果已爬取數量達到實際數量的95%，則傳回true，否則傳回false。
     */
    public static boolean isCrawlingCloseToRealFollowerCount(int amountFromCrawler, int actualAmount, Double rate) {
        if (actualAmount == 0) {
            throw new ApiException(SysCode.ACTUAL_COUNT_IS_ZERO);
        }
        double percentage = (double) amountFromCrawler / actualAmount;
        return percentage >= rate;
    }

    /**
     * 暫停一段隨機5~15秒時間，以模擬真實請求。
     */
    public static void pauseBetweenRequests() {
        int minSeconds = 5;
        int maxSeconds = 15;
        int randomSleepTime = ThreadLocalRandom.current().nextInt(minSeconds, maxSeconds + 1);

        try {
            log.info("暫停 {} 秒以模擬真實請求...", randomSleepTime);
            TimeUnit.SECONDS.sleep(randomSleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("請求間暫停被中斷");
        }
    }

    /**
     * 計算互動率。
     */
    public static double calculateEngagementRate(int likes, int comments, int reshareCounts, int followers, int postAmounts) {
        if (followers <= 0 || postAmounts <= 0) {
            throw new ApiException(SysCode.FOLLOWERS_OR_MEDIA_AMOUNT_IS_ZERO);
        }
        return (double) (likes + comments + reshareCounts) / postAmounts / followers * 100;
    }
}
