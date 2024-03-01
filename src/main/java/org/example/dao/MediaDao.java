package org.example.dao;

import org.example.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface MediaDao extends JpaRepository<Media, Integer>, CustomMediaRepository {
    int countByIgUserId(Integer igUserId);
}