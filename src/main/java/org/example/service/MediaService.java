package org.example.service;


import org.example.entity.IgUser;
import org.example.entity.Media;

import java.util.List;

/**
 * @author Eric.Lee
 * Date:2024/3/1
 */
public interface MediaService extends BaseService<Media> {
    /**
     * 批量插入media
     *
     * @param mediasList 追蹤者列表
     */
    void batchInsertMedias(List<Media> mediasList);

    /**
     * 透過用戶查詢貼文數量
     *
     * @param igUser 用戶
     * @return 追蹤者數量
     */
    int countMediaByIgUser(IgUser igUser);
}