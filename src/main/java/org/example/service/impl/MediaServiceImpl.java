package org.example.service.impl;

import org.example.dao.MediaDao;
import org.example.entity.IgUser;
import org.example.entity.Media;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return mediaDao.countByIgUserId(igUser);
    }

    @Override
    public boolean existsEarlyMediaBeforeCutoff(IgUser igUser, LocalDateTime cutoffDate) {
        return mediaDao.existsEarlyMediaBeforeCutoff(igUser, cutoffDate);
    }

    @Override
    public void deleteOldMediaDataByIgUserId(Integer igUserId) {
        mediaDao.deleteByIgUserId(igUserId);
    }

    @Override
    public List<Media> listMediaByIgUserIdAndDateRange(IgUser igUser, LocalDateTime time) {
        // 如果 time 為 null，則預設為兩週前
        if (time == null) time = LocalDateTime.now().minusWeeks(2);
        List<Media> mediaList = mediaDao.findMediaInTime(igUser, time);
        // 使用Optional来检查列表是否为空
        return Optional.ofNullable(mediaList)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ApiException(SysCode.MEDIA_NOT_FOUND));
    }
}
