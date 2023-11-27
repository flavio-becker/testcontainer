package com.testcontainers.adapters.persistence.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {

    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T entity);
    void delete(T entity);
    void deleteAll();
}
