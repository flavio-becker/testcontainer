package com.testcontainers.adapters.persistence.dynamodb;

import com.testcontainers.adapters.persistence.dao.GenericDao;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Slf4j
public abstract class DynamoDBDao<T, ID> implements GenericDao<T, ID> {

    private final DynamoDbTable<T> dynamoDbTable;

    public DynamoDBDao(DynamoDbEnhancedClient enhancedClient, Class<T> instanceClass, String tableName) {
        this.dynamoDbTable = enhancedClient.table(tableName, TableSchema.fromBean(instanceClass));
    }

    protected abstract Key createKeyFromId(ID id);


    @Override
    public Optional<T> findById(ID id) {
        Key key = createKeyFromId(id);

        return Optional.ofNullable(dynamoDbTable.getItem(r -> r.key(key)));
    }

    @Override
    public T save(T entity) {
        dynamoDbTable.putItem(entity);
        return entity;
    }

    @Override
    public void deleteById(ID id) {
        Key key = createKeyFromId(id);
        dynamoDbTable.deleteItem(r -> r.key(key));
    }

}
