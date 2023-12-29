package com.testcontainers.adapters.persistence.dao;

import java.util.List;
import java.util.Optional;

public interface GenericDao<T, ID> {

    Optional<T> findById(ID id);
    T save(T entity);
    void delete(T entity);
}
