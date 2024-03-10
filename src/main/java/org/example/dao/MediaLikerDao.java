package org.example.dao;

import org.example.entity.MediaLiker;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Eric.Lee
 * Date:2024/3/11
 */
public interface MediaLikerDao extends JpaRepository<MediaLiker, Integer> {
}