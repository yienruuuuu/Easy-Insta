package org.example.dao;

import org.example.entity.MediaComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaCommentDao extends JpaRepository<MediaComment, Integer>, CustomMediaCommentRepository {
    void deleteAllByMediaIdIsIn(List<Integer> mediaIds);
}