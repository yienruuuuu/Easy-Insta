package org.example.dao;

import org.example.entity.IgUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IgUserDao extends JpaRepository<IgUser, Integer> {
    /**
     * 以用戶Pk查詢用戶
     *
     * @param igPk 用戶Pk
     * @return 用戶信息
     */
    IgUser findByIgPk(long igPk);

    /**
     * 以用戶名查詢用戶
     *
     * @param userName 用戶名
     * @return 用戶信息
     */
    Optional<IgUser> findByUserName(String userName);


}