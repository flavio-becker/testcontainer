package com.testcontainers.awstestcontainers;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@EnableSqs
@Configuration
public class SqsConfigTest {

    @Bean
    public LocalStackContainer localStackContainer() {
        LocalStackContainer container = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.0"))
                .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.DYNAMODB)
                .withExposedPorts(4566, 4569); // SQS and DynamoDB ports
        container.start(); // Inicia o contêiner explicitamente
        return container;
    }

    @Primary
    @Bean
    public AmazonSQSAsync amazonSQSAsync(LocalStackContainer localStackContainer) {
        String endpointUrl = "http://" + localStackContainer.getContainerIpAddress() + ":" + localStackContainer.getMappedPort(4566);

        AmazonSQSAsync sqsAsync = AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                endpointUrl,
                                localStackContainer.getRegion()  // ou a região desejada
                        )
                )
                .build();

        // Criar a fila SQS
        String queueName = "fila";
        sqsAsync.createQueue(new CreateQueueRequest(queueName));

        return sqsAsync;
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB(LocalStackContainer localStackContainer) {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "http://" + localStackContainer.getContainerIpAddress() + ":" + localStackContainer.getMappedPort(4569),
                        Regions.DEFAULT_REGION.getName()))
                .build();
    }

    @Primary
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry, LocalStackContainer localStackContainer) {
        registry.add("cloud.aws.sqs.endpoint", () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.SQS));
    }
}
