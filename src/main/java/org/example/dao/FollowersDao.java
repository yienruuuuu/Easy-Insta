package org.example.dao;

import org.example.entity.Followers;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface FollowersDao extends JpaRepository<Followers, Integer>, CustomFollowersRepository {
    int countByIgUserName(String igUserName);
}