package org.example.dao;

import org.example.entity.Followers;
import org.example.entity.IgUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface FollowersDao extends JpaRepository<Followers, Integer>, CustomFollowersRepository {
    /**
     * 透過用戶查詢追蹤者數量
     *
     * @param igUser 用戶
     * @return 追蹤者數量
     */
    int countByIgUser(IgUser igUser);

    /**
     * 透過用戶刪除舊的追蹤者資料
     *
     * @param igUser 用戶
     */
    void deleteByIgUser(IgUser igUser);
}