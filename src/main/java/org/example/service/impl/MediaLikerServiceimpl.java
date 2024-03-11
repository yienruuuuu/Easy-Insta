package org.example.service.impl;

import org.example.dao.MediaLikerDao;
import org.example.entity.MediaLiker;
import org.example.service.MediaLikerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/3/11
 */
@Service("mediaLikerService")
public class MediaLikerServiceimpl implements MediaLikerService {
    private final MediaLikerDao mediaLikerDao;

    public MediaLikerServiceimpl(MediaLikerDao mediaLikerDao) {
        this.mediaLikerDao = mediaLikerDao;
    }

    @Override
    public Optional<MediaLiker> save(MediaLiker target) {
        return Optional.of(mediaLikerDao.save(target));
    }

    @Override
    public Optional<MediaLiker> findById(Integer id) {
        return mediaLikerDao.findById(id);
    }

    @Override
    public List<MediaLiker> findAll() {
        return mediaLikerDao.findAll();
    }

    @Override
    public void deleteOldMediaLikerByIgUserId(List<Integer> mediaIds) {
        mediaLikerDao.deleteAllByMediaIdIsIn(mediaIds);
    }

    @Override
    public void batchInsert(List<MediaLiker> likerList) {
        mediaLikerDao.batchInsertOrUpdate(likerList);
    }
}
