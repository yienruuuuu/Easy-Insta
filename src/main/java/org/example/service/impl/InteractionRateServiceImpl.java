package org.example.service.impl;

import org.example.dao.InteractionRateDao;
import org.example.entity.InteractionRate;
import org.example.service.InteractionRateService;

import java.util.List;
import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2024/3/4
 */
public class InteractionRateServiceImpl implements InteractionRateService {
    private InteractionRateDao interactionRateDao;

    public InteractionRateServiceImpl(InteractionRateDao interactionRateDao) {
        this.interactionRateDao = interactionRateDao;
    }

    @Override
    public Optional<InteractionRate> save(InteractionRate target) {
        return Optional.of(interactionRateDao.save(target));
    }

    @Override
    public Optional<InteractionRate> findById(Integer id) {
        return interactionRateDao.findById(id);
    }

    @Override
    public List<InteractionRate> findAll() {
        return interactionRateDao.findAll();
    }

}
