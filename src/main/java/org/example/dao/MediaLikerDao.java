package org.example.dao;

import org.example.entity.MediaLiker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/11
 */
public interface MediaLikerDao extends JpaRepository<MediaLiker, Integer>, CustomMediaLikerRepository{
    /**
     * 透過用戶ID刪除舊的貼文留言資料
     *
     * @param mediaIds 貼文IDs
     */
    void deleteAllByMediaIdIsIn(List<Integer> mediaIds);
}