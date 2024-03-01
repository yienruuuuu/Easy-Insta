package org.example.dao.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.CustomMediaRepository;
import org.example.entity.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
@Slf4j
@Repository
public class CustomMediaRepositoryImpl implements CustomMediaRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 批量插入，發生衝突則更新
     *
     * @param mediaList 媒體列表
     */
    @Override
    public void batchInsertOrUpdate(List<Media> mediaList) {
        String sql = "INSERT INTO media (media_id, ig_user_id, media_pk, play_count, fb_play_count, like_count, fb_like_count, reshare_count, comment_count, number_of_qualities) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "media_pk = VALUES(media_pk), play_count = VALUES(play_count), fb_play_count = VALUES(fb_play_count), " +
                "like_count = VALUES(like_count), fb_like_count = VALUES(fb_like_count), reshare_count = VALUES(reshare_count), " +
                "comment_count = VALUES(comment_count), number_of_qualities = VALUES(number_of_qualities)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Media media = mediaList.get(i);
                ps.setString(1, media.getMediaId());
                ps.setLong(2, media.getIgUserId().getId()); // 注意，這裡假設 IgUser 實體存在並有 getId() 方法
                ps.setLong(3, media.getMediaPk());
                ps.setInt(4, media.getPlayCount() == null ? 0 : media.getPlayCount());
                ps.setInt(5, media.getFbPlayCount() == null ? 0 : media.getFbPlayCount());
                ps.setInt(6, media.getLikeCount() == null ? 0 : media.getLikeCount());
                ps.setInt(7, media.getFbLikeCount() == null ? 0 : media.getFbLikeCount());
                ps.setInt(8, media.getReshareCount() == null ? 0 : media.getReshareCount());
                ps.setInt(9, media.getCommentCount() == null ? 0 : media.getCommentCount());
                ps.setInt(10, media.getNumberOfQualities() == null ? 0 : media.getNumberOfQualities());
            }

            @Override
            public int getBatchSize() {
                return mediaList.size();
            }
        });
    }
}
