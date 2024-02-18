package org.example.dao;

import org.example.entity.Followers;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface CustomFollowersRepository {
    void batchInsertOrUpdate(List<Followers> followersList);
}
