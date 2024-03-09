package org.example.dao;

import org.example.entity.MediaComment;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/9
 */
public interface CustomMediaCommentRepository {
    void batchInsertOrUpdate(List<MediaComment> commentList);
}
