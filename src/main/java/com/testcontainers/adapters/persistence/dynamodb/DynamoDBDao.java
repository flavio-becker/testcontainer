package com.testcontainers.adapters.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.testcontainers.adapters.persistence.dao.GenericDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class DynamoDBDao<T, ID> implements GenericDao<T, ID> {

    protected final AmazonDynamoDB amazonDynamoDB;

    private final Class<T> instanceClass;

    private final Environment env;

    public DynamoDBDao(AmazonDynamoDB amazonDynamoDB, Class<T> instanceClass, Environment env) {
        this.amazonDynamoDB = amazonDynamoDB;
        this.instanceClass = instanceClass;
        this.env = env;
    }

    @Override
    public Optional<T> findById(ID id) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);

        T entity = mapper.load(instanceClass, id);

        if(entity != null) {
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    @Override
    public T save(T entity) {
        DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
        mapper.save(entity);
        return entity;
    }

    @Override
    public void delete(T entity) {
        DynamoDBMapper mapper= new DynamoDBMapper(amazonDynamoDB);
        mapper.delete(entity);
    }
}
