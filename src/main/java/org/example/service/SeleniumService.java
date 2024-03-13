package org.example.service;

import org.example.entity.Followers;

/**
 * @author Eric.Lee
 * Date: 2024/3/12
 */
public interface SeleniumService {
    /**
     * 透過css選擇器找尋style以爬取追蹤者詳細資訊
     *
     * @param follower 追蹤者
     * @return 追蹤者詳細資訊
     */
    void crawlFollowerDetailByCssStyle(Followers follower);

    boolean isReadyForCrawl();
}
