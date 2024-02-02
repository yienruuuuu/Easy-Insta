package org.example.dao;

import org.example.entity.IgUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IgUserDao extends JpaRepository<IgUser, Integer> {
    IgUser findByIgPk(long igPk);
}