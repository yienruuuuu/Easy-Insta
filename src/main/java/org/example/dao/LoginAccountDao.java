package org.example.dao;

import org.example.bean.enumtype.LoginAccountStatusEnum;
import org.example.entity.LoginAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginAccountDao extends JpaRepository<LoginAccount, Integer> {
    Optional<LoginAccount> findLoginAccountByStatus(LoginAccountStatusEnum status);
}