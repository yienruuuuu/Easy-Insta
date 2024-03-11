package org.example.service;

import org.example.entity.MediaLiker;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/11
 */
public interface MediaLikerService extends BaseService<MediaLiker> {
    /**
     * 透過用戶ID刪除舊的貼文留言資料
     *
     * @param mediaIds 貼文IDs
     */
    void deleteOldMediaLikerByIgUserId(List<Integer> mediaIds);

    /**
     * 批次儲存
     *
     * @param likerList 按讚者列表
     */
    void batchInsert(List<MediaLiker> likerList);
}
