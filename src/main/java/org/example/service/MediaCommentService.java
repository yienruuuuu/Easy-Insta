package org.example.service;

import org.example.entity.Media;
import org.example.entity.MediaComment;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/8
 */
public interface MediaCommentService extends BaseService<MediaComment> {
    /**
     * 透過用戶ID刪除舊的貼文留言資料
     *
     * @param mediaIds 貼文IDs
     */
    void deleteOldMediaCotentDataByIgUserId(List<Integer> mediaIds);

    /**
     * 批次儲存
     *
     * @param commentList 留言列表
     */
    void batchInsertMedias(List<MediaComment> commentList);

    /**
     * 透過貼文ID查詢貼文留言
     *
     * @param mediaList 貼文列表
     * @return 留言列表
     */
    List<MediaComment> findByMediaForCommentReport(List<Media> mediaList);
}
