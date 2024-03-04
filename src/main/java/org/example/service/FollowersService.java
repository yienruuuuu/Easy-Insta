package org.example.service;


import org.example.entity.Followers;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface FollowersService extends BaseService<Followers> {
    /**
     * 批量插入追蹤者
     *
     * @param followersList 追蹤者列表
     */
    void batchInsertFollowers(List<Followers> followersList);

    /**
     * 透過用戶ID查詢追蹤者數量
     *
     * @param userId 用戶ID
     * @return 追蹤者數量
     */
    int countFollowersByIgUserName(String userId);
}