package com.testcontainers.awstestcontainers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.testcontainers.domain.entities.Employee;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@TestConfiguration
@TestPropertySource(properties = {
        "amazon.aws.accesskey=test1",
        "amazon.aws.secretkey=test231"
})
public class LocalStackTestcontainers {

    @Autowired
    AmazonDynamoDB amazonDynamoDB;

    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.0"))
                    .withServices(DYNAMODB)
                    .withNetworkAliases("localstack")
                    .withNetwork(Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("test-net")).build());

    static {
        localStack.start();
        System.setProperty("amazon.dynamodb.endpoint", localStack.getEndpointOverride(DYNAMODB).toString());

    }

    @PostConstruct
    public void init() {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableUserRequest = dynamoDBMapper
                .generateCreateTableRequest(Employee.class);

        tableUserRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));

        amazonDynamoDB.createTable(tableUserRequest);
       }
}
