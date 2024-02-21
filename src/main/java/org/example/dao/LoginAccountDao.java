package org.example.dao;

import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.entity.LoginAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginAccountDao extends JpaRepository<LoginAccount, Integer> {
    Optional<LoginAccount> findLoginAccountByStatus(LoginAccountStatusEnum status);

    /**
     * 更新所有在指定時間之前的狀態為oldStatus的帳號為newStatus
     *
     * @param thresholdTime 時間閾值
     * @param newStatus     新狀態
     * @param oldStatus     舊狀態
     * @return 更新筆數
     */
    @Modifying
    @Transactional
    @Query("UPDATE LoginAccount la SET la.status = :newStatus, la.modifyTime = CURRENT_TIMESTAMP WHERE la.status = :oldStatus AND la.modifyTime < :thresholdTime")
    int updateStatusForExhaustedAccountsBefore(@Param("thresholdTime") LocalDateTime thresholdTime, @Param("newStatus") LoginAccountStatusEnum newStatus, @Param("oldStatus") LoginAccountStatusEnum oldStatus);
}