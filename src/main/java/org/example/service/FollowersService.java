package org.example.service;


import org.example.entity.Followers;
import org.example.entity.IgUser;

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
     * @param igUser 用戶
     * @return 追蹤者數量
     */
    int countFollowersByIgUserName(IgUser igUser);

    /**
     * 透過用戶刪除舊的追蹤者資料
     *
     * @param igUser 用戶
     */
    void deleteOldFollowersDataByIgUser(IgUser igUser);

    /**
     * 透過用戶名稱查詢追蹤者明細
     *
     * @param iguser 用戶
     */
    void getFollowersDetailByIgUserName(IgUser iguser);
}