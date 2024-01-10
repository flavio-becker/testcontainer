package com.testcontainers.adapters.persistence.dynamodb;

import com.testcontainers.adapters.persistence.dao.EmployeeDao;
import com.testcontainers.domain.entities.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
@Slf4j
public class EmployeeDynamoDBDao extends DynamoDBDao<Employee, String> implements EmployeeDao {

    static final String NOME_TABELA = "employee";

    public EmployeeDynamoDBDao(DynamoDbEnhancedClient enhancedClient) {
        super(enhancedClient, Employee.class, NOME_TABELA);
    }

    @Override
    protected Key createKeyFromId(String id) {
        return Key.builder()
                .partitionValue(id)
                .build();
    }
}
