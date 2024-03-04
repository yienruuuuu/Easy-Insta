package org.example.dao;

import org.example.entity.InteractionRate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface InteractionRateDao extends JpaRepository<InteractionRate, Integer> {
}