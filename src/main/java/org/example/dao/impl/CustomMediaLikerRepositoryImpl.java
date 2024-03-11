package org.example.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.CustomMediaLikerRepository;
import org.example.entity.MediaLiker;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/11
 */
@Slf4j
@Repository
public class CustomMediaLikerRepositoryImpl implements CustomMediaLikerRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomMediaLikerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void batchInsertOrUpdate(List<MediaLiker> mediaLikerList) {
        String sql = "INSERT INTO media_liker (media_id, liker_user_name, liker_full_name, liker_pk, liker_is_private, liker_is_verified, liker_profile_pic_id, liker_profile_pic_url, liker_latest_reel_media) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "liker_user_name = VALUES(liker_user_name), " + // 更新一個不變的欄位或使用其它邏輯來確保記錄不變
                "liker_user_name = liker_user_name"; // 實際上這行不會改變任何數據，只是為了符合語法

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                MediaLiker mediaLiker = mediaLikerList.get(i);
                // 設置PreparedStatement的值
                ps.setInt(1, mediaLiker.getMedia().getId());
                ps.setString(2, mediaLiker.getLikerUserName());
                ps.setString(3, mediaLiker.getLikerFullName());
                ps.setLong(4, mediaLiker.getLikerPk());
                ps.setBoolean(5, mediaLiker.isLikerIsPrivate());
                ps.setBoolean(6, mediaLiker.isLikerIsVerified());
                ps.setString(7, mediaLiker.getLikerProfilePicId());
                ps.setString(8, mediaLiker.getLikerProfilePicUrl());
                ps.setLong(9, mediaLiker.getLikerLatestReelMedia());
            }
            @Override
            public int getBatchSize() {
                return mediaLikerList.size();
            }
        });
    }
}
