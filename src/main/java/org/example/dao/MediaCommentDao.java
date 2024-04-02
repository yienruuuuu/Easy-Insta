package org.example.dao;

import org.example.bean.dto.CommentReportDto;
import org.example.entity.IgUser;
import org.example.entity.Media;
import org.example.entity.MediaComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MediaCommentDao extends JpaRepository<MediaComment, Integer>, CustomMediaCommentRepository {
    void deleteAllByMediaIdIsIn(List<Integer> mediaIds);

    /**
     * 透過貼文ID查詢貼文留言
     *
     * @param medias 貼文列表
     * @return 留言列表
     */
    List<MediaComment> findAllByMediaIn(List<Media> medias);

    /**
     * 根據media_id_list查詢留言統計資料
     *
     * @return 統計列表
     */
    @Query("SELECT new org.example.bean.dto.CommentReportDto(" +
            "mc.commenterUserName, COUNT(mc), SUM(mc.commentLikeCount)) " +
            "FROM MediaComment mc " +
            "WHERE mc.media.igUserId = :igUserId " +
            "GROUP BY mc.commenterUserName " +
            "ORDER BY COUNT(mc) DESC")
    List<CommentReportDto> findCommentSummaryByIgUserId(@Param("igUserId") IgUser igUser);


}