package org.example.service.impl;

import org.example.dao.FollowersDao;
import org.example.entity.Followers;
import org.example.entity.IgUser;
import org.example.exception.ApiException;
import org.example.exception.SysCode;
import org.example.service.FollowersService;
import org.example.service.SeleniumService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date:2024/2/18
 */
@Service("followersService")
public class FollowersServiceImpl implements FollowersService {
    private final FollowersDao followersDao;
    private final SeleniumService seleniumService;

    public FollowersServiceImpl(FollowersDao followersDao, SeleniumService seleniumService) {
        this.followersDao = followersDao;
        this.seleniumService = seleniumService;
    }


    @Override
    public void batchInsertFollowers(List<Followers> followersList) {
        followersDao.batchInsertOrUpdate(followersList);
    }

    @Override
    public int countFollowersByIgUserName(IgUser igUser) {
        return followersDao.countByIgUser(igUser);
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

    @Override
    public void deleteOldFollowersDataByIgUser(IgUser igUser) {
        followersDao.deleteByIgUser(igUser);
    }

    @Override
    public List<Followers> findByIgUser(IgUser igUser) {
        List<Followers> followersList = followersDao.findByIgUser(igUser);
        if (followersList.isEmpty()) {
            throw new ApiException(SysCode.FOLLOWERS_OR_MEDIA_AMOUNT_IS_ZERO);
        }
        return followersList;
    }

    @Override
    public void getFollowersDetailByIgUserName(IgUser igUser) {
        List<Followers> followersList = followersDao.findByIgUser(igUser);
        if (followersList.isEmpty()) {
            throw new ApiException(SysCode.FOLLOWERS_OR_MEDIA_AMOUNT_IS_ZERO);
        }
        processFollowersInBatches(followersList, 10);
    }

    // private

    private void processFollowersInBatches(List<Followers> followersList, int batchSize) {
        int totalBatches = (followersList.size() + batchSize - 1) / batchSize;
        for (int batch = 0; batch < totalBatches; batch++) {
            int start = batch * batchSize;
            int end = Math.min(start + batchSize, followersList.size());
            List<Followers> batchList = followersList.subList(start, end);
            seleniumService.crawlFollowerDetailByCssStyle(batchList);
        }
    }
}
