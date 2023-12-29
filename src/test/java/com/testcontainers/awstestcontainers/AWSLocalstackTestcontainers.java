package com.testcontainers.awstestcontainers;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.testcontainers.domain.entities.Employee;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Slf4j
public class AWSLocalstackTestcontainers implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Autowired
    AmazonDynamoDB amazonDynamoDB;


    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.0"))
            .withServices(SQS, LocalStackContainer.Service.DYNAMODB)
            .withExposedPorts(4566, 4569)
            .withEnv("LOCALSTACK_HOST", "localhost")
            .withNetworkAliases("localstack")
            .withNetwork(Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("test-net")).build())
            .withStartupTimeout(Duration.ofMinutes(2))
            .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("LocalStack")); // SQS and DynamoDB ports


    static {
        localStackContainer.start();
    }


    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(

                //DYNAMODB CONFIGURATIONS PROPERTIES
                "amazon.dynamodb.endpoint=" + localStackContainer.getEndpointOverride(DYNAMODB).toString(),
                "cloud.aws.dynamodb.endpoint=" + localStackContainer.getEndpointOverride(DYNAMODB).toString(),
                "amazon.aws.accesskey=" + localStackContainer.getSecretKey(),
                "amazon.aws.secretkey=" + localStackContainer.getAccessKey(),
                "spring.cloud.aws.region.static="+ localStackContainer.getRegion(),
                "spring.cloud.aws.credentials.access-key=" + localStackContainer.getAccessKey(),
                "spring.cloud.aws.credentials.secret-key=" + localStackContainer.getSecretKey(),
                "spring.cloud.aws.sqs.endpoint=" + localStackContainer.getEndpointOverride(SQS).toString(),
                "cloud.aws.sqs.default.queue.message.handler.enabled=" + "false",
                "cloud.aws.sqs.endpoint=" + localStackContainer.getEndpointOverride(SQS).toString()

        ).applyTo(ctx.getEnvironment());
    }

    @PostConstruct
    public void init() {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableEmployeeRequest = dynamoDBMapper
                .generateCreateTableRequest(Employee.class);
        tableEmployeeRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));

        try {
            amazonDynamoDB.createTable(tableEmployeeRequest);
        } catch (Throwable e) {
            log.info("Banco ja criada");
        }
    }


    @Slf4j
    @Configuration
    static class SqsConfigTest {

        @Primary
        @Bean
        public AmazonSQSAsync amazonSQSAsync() {
            String endpointUrl = localStackContainer.getEndpointOverride(SQS).toString();
            String accessKey = localStackContainer.getAccessKey();
            String secretKey = localStackContainer.getSecretKey();
            log.info("Endpoint SQS {}", localStackContainer.getEndpointOverride(SQS).toString());

            AmazonSQSAsync sqsAsync = AmazonSQSAsyncClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    endpointUrl,
                                    localStackContainer.getRegion()))
                    .withClientConfiguration(new ClientConfiguration().withRequestTimeout(50000)) // Ajuste conforme necessário
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))) // Substitua com suas credenciais
                    .build();

            // Criar a fila SQS
            String queueName = "fila";
            CreateQueueResult queueResult = sqsAsync.createQueue(new CreateQueueRequest(queueName));
            log.info("Fila SQS criada com sucesso: {}", queueResult);

            return sqsAsync;
        }

        @Primary
        @Bean
        public AmazonDynamoDB amazonDynamoDB() {
            String endpointUrl = localStackContainer.getEndpointOverride(DYNAMODB).toString();
            String accessKey = localStackContainer.getAccessKey();
            String secretKey = localStackContainer.getSecretKey();
            log.info("Endpoint Dynamo {}", localStackContainer.getEndpointOverride(DYNAMODB).toString());

            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl,
                            localStackContainer.getRegion()))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();
        }

        @Primary
        @Bean
        public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
            return new QueueMessagingTemplate(amazonSQSAsync);
        }

    }
}