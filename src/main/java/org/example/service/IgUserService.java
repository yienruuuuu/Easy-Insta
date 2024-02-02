package org.example.service;


import org.example.entity.IgUser;

import java.util.Optional;

public interface IgUserService extends BaseService<IgUser> {
    Optional<IgUser> findUserByIgPk(long igPk);

}