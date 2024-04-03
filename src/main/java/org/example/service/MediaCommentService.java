package org.example.service;

import org.example.bean.dto.CommentReportDto;
import org.example.bean.dto.MediaCommentDetailDto;
import org.example.entity.IgUser;
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

    /**
     * 查詢留言統計資料
     *
     * @return 統計列表
     */
    List<CommentReportDto> findCommentSummary(IgUser igUser);

    /**
     * 查詢留言詳細資料
     *
     * @return 列表
     */
    List<MediaCommentDetailDto> findCommentDetail(IgUser igUser);
}
