package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.ApiException;
import org.example.exception.SysCode;

/**
 * @author Eric.Lee
 * Date: 2024/2/23
 */
@Slf4j
public class FollowerCrawlingUtil {
    /**
     * 检查已爬取的追蹤者数量是否达到实际追蹤者数量的95%。
     *
     * @param amountFromCrawler 已爬取的追蹤者数量
     * @param actualAmount      实际的追蹤者数量
     * @return 如果已爬取數量達到實際數量的95%，則傳回true，否則傳回false。
     */
    public static boolean isCrawlingCloseToRealFollowerCount(int amountFromCrawler, int actualAmount) {
        if (actualAmount == 0) {
            throw new ApiException(SysCode.ACTUAL_COUNT_IS_ZERO);
        }
        double percentage = (double) amountFromCrawler / actualAmount;
        return percentage >= 0.95;
    }
}
