package com.testcontainers.adapters.persistence.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.testcontainers.adapters.persistence.dao.EmployeeDao;
import com.testcontainers.domain.entities.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class EmployeeDynamoDBDao extends DynamoDBDao<Employee, Integer> implements EmployeeDao {

    public EmployeeDynamoDBDao(AmazonDynamoDB amazonDynamoDB, Environment env) {
        super(amazonDynamoDB, Employee.class, env);
    }
}
