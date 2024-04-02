package org.example.service.impl;

import org.example.dao.MediaDao;
import org.example.entity.IgUser;
import org.example.entity.Media;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.MediaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Eric.Lee
 * Date: 2024/3/1
 */
@Service
public class MediaServiceImpl implements MediaService {
    private final MediaDao mediaDao;
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

    public MediaServiceImpl(MediaDao mediaDao) {
        this.mediaDao = mediaDao;
    }

    @Override
    public Optional<Media> save(Media target) {
        return Optional.of(mediaDao.save(target));
    }

    @Override
    public Optional<Media> findById(Integer id) {
        return mediaDao.findById(id);
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

    @Override
    public List<Media> listMediaByIgUserIdAndCommentCount(IgUser igUser, int commentCount) {
        List<Media> mediaList = mediaDao.findAllByIgUserIdAndCommentCount(igUser, commentCount);
        return Optional.ofNullable(mediaList)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ApiException(SysCode.MEDIA_NOT_FOUND));
    }

    @Override
    public LinkedHashMap<String, Long> analyzeHashtagsAndSort(IgUser igUser) {
        List<Media> medias = listMediaByIgUserIdAndCommentCount(igUser, 0);

        Map<String, Long> hashtagFrequencyMap = medias.stream()
                .map(Media::getText)
                .filter(Objects::nonNull)
                .flatMap(text -> {
                    Matcher matcher = HASHTAG_PATTERN.matcher(text);
                    List<String> hashtags = new ArrayList<>();
                    while (matcher.find()) {
                        hashtags.add("#" + matcher.group(1));
                    }
                    return hashtags.stream();
                })
                .collect(Collectors.groupingBy(hashtag -> hashtag, Collectors.counting()));

        return hashtagFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
