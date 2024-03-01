package org.example.dao;

import org.example.entity.Media;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
public interface CustomMediaRepository {
    void batchInsertOrUpdate(List<Media> mediasList);
}
