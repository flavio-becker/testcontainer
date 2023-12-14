package com.testcontainers.domain.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName = "Employees")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private String id;

    private String firstname;


    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "Id")
    public String getId() {
        return id;
    }

    @DynamoDBAttribute(attributeName = "Firstname")
    public String getFirstname() {
        return firstname;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}


