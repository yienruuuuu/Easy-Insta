package org.example.dao;

import org.example.entity.IgUser;
import org.example.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface MediaDao extends JpaRepository<Media, Integer>, CustomMediaRepository {
    int countByIgUserId(IgUser igUser);

    // 檢查是否存在最早的貼文日期大於cutoffDate
    @Query("SELECT COUNT(m) > 0 FROM Media m WHERE m.igUserId = :igUserId AND m.takenAt > :cutoffDate")
    boolean existsEarlyMediaBeforeCutoff(@Param("igUserId") IgUser igUserId, @Param("cutoffDate") LocalDateTime cutoffDate);

    // 透過用戶ID刪除舊的貼文資料
    @Modifying
    @Query("delete from Media m where m.igUserId.id = :igUserId")
    void deleteByIgUserId(@Param("igUserId") Integer igUserId);
}