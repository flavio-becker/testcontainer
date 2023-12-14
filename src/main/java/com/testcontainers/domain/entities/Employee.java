package com.testcontainers.domain.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.With;

@DynamoDBTable(tableName = "Employees")
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private String id;

    private String firstname;
    private String saldo;


    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "Id")
    public String getId() {
        return id;
    }

    @DynamoDBAttribute(attributeName = "Firstname")
    public String getFirstname() {
        return firstname;
    }

    @DynamoDBAttribute(attributeName = "Saldo")
    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}


