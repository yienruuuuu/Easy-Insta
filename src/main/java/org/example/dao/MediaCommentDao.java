package org.example.dao;

import org.example.entity.Media;
import org.example.entity.MediaComment;
import org.springframework.data.jpa.repository.JpaRepository;

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
}