package org.example.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.CustomMediaCommentRepository;
import org.example.entity.MediaComment;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/9
 */
@Slf4j
@Repository
public class CustomMediaCommentRepositoryImpl implements CustomMediaCommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public CustomMediaCommentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 批量插入，發生衝突則更新
     *
     * @param mediaCommentList 媒體列表
     */
    @Override
    public void batchInsertOrUpdate(List<MediaComment> mediaCommentList) {
        String sql = "INSERT INTO media_comment (media_id, text, commenter_full_name, commenter_user_id, commenter_user_name, comment_pk, commenter_is_private, commenter_is_verified, commenter_profile_pic_id, commenter_profile_pic_url, commenter_latest_reel_media, content_type, status, comment_like_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "text = VALUES(text), commenter_full_name = VALUES(commenter_full_name), commenter_user_id = VALUES(commenter_user_id), " +
                "commenter_user_name = VALUES(commenter_user_name), commenter_is_private = VALUES(commenter_is_private), commenter_is_verified = VALUES(commenter_is_verified), " +
                "commenter_profile_pic_id = VALUES(commenter_profile_pic_id), commenter_profile_pic_url = VALUES(commenter_profile_pic_url), commenter_latest_reel_media = VALUES(commenter_latest_reel_media), " +
                "content_type = VALUES(content_type), status = VALUES(status), comment_like_count = VALUES(comment_like_count)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MediaComment mediaComment = mediaCommentList.get(i);
                ps.setInt(1, mediaComment.getMedia().getId());
                ps.setString(2, mediaComment.getText());
                ps.setString(3, mediaComment.getCommenterFullName());
                ps.setLong(4, mediaComment.getCommenterUserId());
                ps.setString(5, mediaComment.getCommenterUserName());
                ps.setString(6, mediaComment.getCommentPk());
                ps.setBoolean(7, mediaComment.isCommenterIsPrivate());
                ps.setBoolean(8, mediaComment.isCommenterIsVerified());
                ps.setString(9, mediaComment.getCommenterProfilePicId());
                ps.setString(10, mediaComment.getCommenterProfilePicUrl());
                ps.setLong(11, mediaComment.getCommenterLatestReelMedia());
                ps.setString(12, mediaComment.getContentType());
                ps.setString(13, mediaComment.getStatus());
                ps.setInt(14, mediaComment.getCommentLikeCount());
            }

            @Override
            public int getBatchSize() {
                return mediaCommentList.size();
            }
        });
    }
}
