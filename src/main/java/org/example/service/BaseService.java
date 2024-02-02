package org.example.service;

import java.util.List;
import java.util.Optional;

public interface BaseService<T> {

    Optional<T> save(T target);

    Optional<T> findById(Integer id);

    List<T> findAll();
}