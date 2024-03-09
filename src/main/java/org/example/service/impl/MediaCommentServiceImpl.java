package org.example.service.impl;

import org.example.dao.MediaCommentDao;
import org.example.entity.MediaComment;
import org.example.service.MediaCommentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/3/8
 */
@Service("mediaCommentService")
public class MediaCommentServiceImpl implements MediaCommentService {
    private final MediaCommentDao mediaCommentDao;

    public MediaCommentServiceImpl(MediaCommentDao mediaCommentDao) {
        this.mediaCommentDao = mediaCommentDao;
    }

    @Override
    public Optional<MediaComment> save(MediaComment target) {
        return Optional.of(mediaCommentDao.save(target));
    }

    @Override
    public Optional<MediaComment> findById(Integer id) {
        return mediaCommentDao.findById(id);
    }

    @Override
    public List<MediaComment> findAll() {
        return mediaCommentDao.findAll();
    }

    @Override
    public void deleteOldMediaCotentDataByIgUserId(List<Integer> mediaIds) {
        mediaCommentDao.deleteAllByMediaIdIsIn(mediaIds);
    }

    @Override
    public void batchInsertMedias(List<MediaComment> commentList) {
        mediaCommentDao.batchInsertOrUpdate(commentList);
    }
}
