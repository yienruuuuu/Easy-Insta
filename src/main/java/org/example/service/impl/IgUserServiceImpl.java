package org.example.service.impl;

import org.example.dao.IgUserDao;
import org.example.entity.IgUser;
import org.example.service.IgUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("igUserImpl")
public class IgUserServiceImpl implements IgUserService {
    @Autowired
    IgUserDao userDao;

    @Override
    public Optional<IgUser> save(IgUser target) {
        return Optional.of(userDao.save(target));
    }

    @Override
    public Optional<IgUser> findById(Integer id) {
        return Optional.of(userDao.findById(id).get());
    }

    @Override
    public List<IgUser> findAll() {
        return Optional.of(userDao.findAll()).get();
    }

    @Override
    public Optional<IgUser> findUserByIgPk(long igPk) {
        return userDao.findByIgPk(igPk);
    }

    @Override
    public Optional<IgUser> findUserByIgUserName(String igUserName) {
        return userDao.findByUserName(igUserName);
    }

    @Override
    public IgUser saveOrUpdateIgUser(IgUser newUser) {
        return userDao.findByIgPk(newUser.getIgPk())
                .map(existingUser -> {
                    // 更新現有用戶的信息
                    existingUser.setUserName(newUser.getUserName());
                    existingUser.setFullName(newUser.getFullName());
                    existingUser.setMediaCount(newUser.getMediaCount());
                    existingUser.setFollowerCount(newUser.getFollowerCount());
                    existingUser.setFollowingCount(newUser.getFollowingCount());
                    return userDao.save(existingUser);
                })
                .orElseGet(() -> userDao.save(newUser)); // 如果沒有找到，儲存新用戶
    }
}
