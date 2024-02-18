package org.example.dao.Impl;

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
@Repository
public class CustomFollowersRepositoryImpl implements CustomFollowersRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void batchInsertOrUpdate(List<Followers> followersList) {
        String sql = "INSERT IGNORE INTO followers (ig_user_name, follower_pk, follower_user_name, follower_full_name) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Followers follower = followersList.get(i);
                ps.setString(1, follower.getIgUserName());
                ps.setLong(2, follower.getFollowerPk());
                ps.setString(3, follower.getFollowerUserName());
                ps.setString(4, follower.getFollowerFullName());
            }

            @Override
            public int getBatchSize() {
                return followersList.size();
            }
        });
    }
}