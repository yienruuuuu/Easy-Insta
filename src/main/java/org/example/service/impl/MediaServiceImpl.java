package org.example.service.impl;

import org.example.dao.MediaDao;
import org.example.entity.IgUser;
import org.example.entity.Media;
import org.example.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/3/1
 */
@Service
public class MediaServiceImpl implements MediaService {
    @Autowired
    MediaDao mediaDao;

    @Override
    public Optional<Media> save(Media target) {
        return Optional.of(mediaDao.save(target));
    }

    @Override
    public Optional<Media> findById(Integer id) {
        return Optional.of(mediaDao.findById(id).get());
    }

    @Override
    public List<Media> findAll() {
        return mediaDao.findAll();
    }

    @Override
    public void batchInsertMedias(List<Media> mediasList) {
        mediaDao.batchInsertOrUpdate(mediasList);
    }

    @Override
    public int countMediaByIgUser(IgUser igUser) {
        return mediaDao.countByIgUserId(igUser.getId());
    }
}
