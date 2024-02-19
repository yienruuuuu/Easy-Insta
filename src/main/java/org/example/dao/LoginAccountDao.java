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

    @Modifying
    @Transactional
    @Query("UPDATE LoginAccount la SET la.status = :newStatus, la.modifyTime = CURRENT_TIMESTAMP WHERE la.status = :oldStatus AND la.modifyTime < :thresholdTime")
    int updateStatusForExhaustedAccountsBefore(@Param("thresholdTime") LocalDateTime thresholdTime, @Param("newStatus") LoginAccountStatusEnum newStatus, @Param("oldStatus") LoginAccountStatusEnum oldStatus);
}