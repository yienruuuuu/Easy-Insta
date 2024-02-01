package org.example.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface BaseService<T> {

    Optional<T> save(T target);

    Optional<T> findById(BigInteger id);

    List<T> findAll();
}