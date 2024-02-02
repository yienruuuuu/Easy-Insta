package org.example.dao;

import org.example.entity.LoginAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAccountDao extends JpaRepository<LoginAccount, Integer> {
}