package org.example.service;


import org.example.entity.Followers;
import org.example.entity.LoginAccount;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface FollowersService extends BaseService<Followers> {
    /**
     * 批量插入追蹤者
     * @param followersList 追蹤者列表
     */
    void batchInsertFollowers(List<Followers> followersList);
}