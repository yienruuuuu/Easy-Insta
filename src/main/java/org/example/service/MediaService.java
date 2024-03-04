package org.example.service;


import org.example.entity.IgUser;
import org.example.entity.Media;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/1
 */
public interface MediaService extends BaseService<Media> {
    /**
     * 批量插入media
     *
     * @param mediasList 追蹤者列表
     */
    void batchInsertMedias(List<Media> mediasList);

    /**
     * 透過用戶查詢貼文數量
     *
     * @param igUser 用戶
     * @return 追蹤者數量
     */
    int countMediaByIgUser(IgUser igUser);

    /**
     * 檢查是否存在最早的貼文日期大於輸入日期參數
     *
     * @param igUser     用戶
     * @param cutoffDate 截止日期
     * @return 是否存在
     */
    boolean existsEarlyMediaBeforeCutoff(IgUser igUser, LocalDateTime cutoffDate);

    /**
     * 透過用戶ID刪除舊的貼文資料
     *
     * @param igUserId 用戶ID
     */
    void deleteOldMediaDataByIgUserId(Integer igUserId);

    /**
     * 透過用戶ID及日期區間查詢貼文列表
     *
     * @param igUser 用戶
     * @param time   時間限制
     * @return 貼文列表
     */
    List<Media> listMediaByIgUserIdAndDateRange(IgUser igUser, LocalDateTime time);
}