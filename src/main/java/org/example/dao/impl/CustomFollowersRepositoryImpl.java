package org.example.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.CustomFollowersRepository;
import org.example.entity.Followers;
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
public class CustomFollowersRepositoryImpl implements CustomFollowersRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsertOrUpdate(List<Followers> followersList) {
        String sql = "INSERT INTO followers (ig_user_id, follower_pk, follower_user_name, follower_full_name, is_private, profile_pic_url, profile_pic_id, is_verified, has_anonymous_profile_picture, latest_reel_media) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "follower_user_name = VALUES(follower_user_name), " +
                "follower_full_name = VALUES(follower_full_name), " +
                "is_private = VALUES(is_private), " +
                "profile_pic_url = VALUES(profile_pic_url), " +
                "profile_pic_id = VALUES(profile_pic_id), " +
                "is_verified = VALUES(is_verified), " +
                "has_anonymous_profile_picture = VALUES(has_anonymous_profile_picture), " +
                "latest_reel_media = VALUES(latest_reel_media)";

        int[] updateCounts = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Followers follower = followersList.get(i);
                ps.setInt(1, follower.getIgUser().getId());
                ps.setLong(2, follower.getFollowerPk());
                ps.setString(3, follower.getFollowerUserName());
                ps.setString(4, follower.getFollowerFullName());
                ps.setBoolean(5, follower.getIsPrivate());
                ps.setString(6, follower.getProfilePicUrl());
                ps.setString(7, follower.getProfilePicId());
                ps.setBoolean(8, follower.getIsVerified());
                ps.setBoolean(9, follower.getHasAnonymousProfilePicture());
                ps.setLong(10, follower.getLatestReelMedia());
            }

            @Override
            public int getBatchSize() {
                return followersList.size();
            }
        });
        // 計算實際寫入的記錄數
        int actualInsertCount = 0;
        for (int count : updateCounts) {
            if (count > 0) {
                actualInsertCount++;
            }
        }
        log.info("實際寫入的記錄數: {}", actualInsertCount);
    }
}
