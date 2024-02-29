package org.example.dao;

import org.example.bean.enumtype.ConfigEnum;
import org.example.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface ConfigDao extends JpaRepository<Config, String> {
    Optional<Config> findByParam(String param);
}