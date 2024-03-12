package org.example.service;

import org.example.entity.Followers;

import java.util.List;

/**
 * @author Eric.Lee
 * Date: 2024/3/12
 */
public interface SeleniumService {
    List<Followers> crawlFollowerDetailByCssStyle(List<Followers> followersList);
}
