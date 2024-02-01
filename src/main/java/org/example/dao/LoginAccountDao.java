package org.example.dao;

import org.example.entity.LoginAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface LoginAccountDao extends JpaRepository<LoginAccount, BigInteger> {
}