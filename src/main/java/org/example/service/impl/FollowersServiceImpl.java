package org.example.service.impl;

import org.example.dao.FollowersDao;
import org.example.entity.Followers;
import org.example.service.FollowersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
@Service("followersService")
public class FollowersServiceImpl implements FollowersService {
    @Autowired
    FollowersDao followersDao;


    @Override
    public void batchInsertFollowers(List<Followers> followersList) {
        followersDao.batchInsertOrUpdate(followersList);
    }

    @Override
    public int countFollowersByIgUserName(String igUserName) {
        return followersDao.countByIgUserName(igUserName);
    }

    @Override
    public Optional<Followers> save(Followers target) {
        return Optional.of(followersDao.save(target));
    }

    @Override
    public Optional<Followers> findById(Integer id) {
        return Optional.of(followersDao.findById(id).get());
    }

    @Override
    public List<Followers> findAll() {
        return followersDao.findAll();
    }
}
