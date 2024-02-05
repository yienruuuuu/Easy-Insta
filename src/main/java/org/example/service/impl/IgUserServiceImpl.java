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
        return Optional.ofNullable(userDao.findByIgPk(igPk));
    }
}
