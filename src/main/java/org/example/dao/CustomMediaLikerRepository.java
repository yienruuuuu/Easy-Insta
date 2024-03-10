package org.example.dao;

import org.example.entity.Media;
import org.example.entity.MediaLiker;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/11
 */
public interface CustomMediaLikerRepository {
    void batchInsertOrUpdate(List<MediaLiker> mediaLikerList);
}
