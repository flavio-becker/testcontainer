package com.testcontainers.awstestcontainers;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.testcontainers.domain.entities.Employee;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
import io.awspring.cloud.messaging.support.destination.DynamicQueueUrlDestinationResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.core.CachingDestinationResolverProxy;
import org.springframework.messaging.core.DestinationResolver;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.time.Duration;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Slf4j
public class AWSLocalstackTestcontainers implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Autowired
    DynamoDbClient dynamoDbClient;

    static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.0"))
            .withServices(SQS, LocalStackContainer.Service.DYNAMODB)
            .withExposedPorts(4566, 4569)
            .withEnv("LOCALSTACK_HOST", "localhost")
            .withNetworkAliases("localstack")
            .withNetwork(Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("test-net")).build())
            .withStartupTimeout(Duration.ofMinutes(5))
            .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("LocalStack")); // SQS and DynamoDB ports


    static {
        localStackContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        if (localStackContainer != null && localStackContainer.isRunning()) {
            localStackContainer.stop();
        }
    }


    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(

                //DYNAMODB CONFIGURATIONS PROPERTIES
                "amazon.dynamodb.endpoint=" + localStackContainer.getEndpointOverride(DYNAMODB).toString(),
                "cloud.aws.dynamodb.endpoint=" + localStackContainer.getEndpointOverride(DYNAMODB).toString(),
                "amazon.aws.accesskey=" + localStackContainer.getSecretKey(),
                "amazon.aws.secretkey=" + localStackContainer.getAccessKey(),
                "spring.cloud.aws.region.static=" + localStackContainer.getRegion(),
                "spring.cloud.aws.credentials.access-key=" + localStackContainer.getAccessKey(),
                "spring.cloud.aws.credentials.secret-key=" + localStackContainer.getSecretKey(),
                "spring.cloud.aws.sqs.endpoint=" + localStackContainer.getEndpointOverride(SQS).toString(),
                "cloud.aws.sqs.default.queue.message.handler.enabled=" + "true",
                "cloud.aws.sqs.listener.auto-startup = true",
                "cloud.aws.sqs.endpoint=" + localStackContainer.getEndpointOverride(SQS).toString()

        ).applyTo(ctx.getEnvironment());
    }

    @Slf4j
    @Profile("test")
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
//            String queueName = "fila";
//            CreateQueueResult queueResult = sqsAsync.createQueue(new CreateQueueRequest(queueName));
//            log.info("Fila SQS criada com sucesso: {}", queueResult);

            return sqsAsync;
        }

        @Primary
        @Bean
        public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
            return new QueueMessagingTemplate(amazonSQSAsync);
        }
    }

    @Configuration
    @Profile("test")
    static class DynamoDbTestConfig {

        @Bean
        public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
            return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        }

        @Bean
        public DynamoDbClient dynamoDbClient() {
            return DynamoDbClient.builder()
                    .region(Region.of(localStackContainer.getRegion())) // Substitua pela região desejada
                    .credentialsProvider(() -> AwsBasicCredentials.create(localStackContainer.getAccessKey(), localStackContainer.getSecretKey()))
                    .endpointOverride(localStackContainer.getEndpointOverride(DYNAMODB))
                    .build();
        }

        @Bean
        public BeanPostProcessor simpleMessageListenerContainerPostProcessor(DestinationResolver<String> destinationResolver) {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                    if (bean instanceof SimpleMessageListenerContainer container) {
                        container.setDestinationResolver(destinationResolver);
                    }
                    return bean;
                }
            };
        }

        /**
         * Creates a DynamicQueueUrlDestinationResolver capable of auto-creating
         * a SQS queue in case it does not exist
         */
        @Bean
        public DestinationResolver<String> autoCreateQueueDestinationResolver(
                AmazonSQSAsync sqs,
                @Autowired(required = false) ResourceIdResolver resourceIdResolver) {

            DynamicQueueUrlDestinationResolver autoCreateQueueResolver
                    = new DynamicQueueUrlDestinationResolver(sqs, resourceIdResolver);
            autoCreateQueueResolver.setAutoCreate(true);

            return new CachingDestinationResolverProxy<>(autoCreateQueueResolver);
        }

        @Bean
        public DynamoDbTable<Employee> employeeDynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
            String TABLE_NAME = "employee";

            BeanTableSchema<Employee> tableSchema = TableSchema.fromBean(Employee.class);
            DynamoDbTable<Employee> table = dynamoDbEnhancedClient.table(TABLE_NAME, tableSchema);

            try {
                table.createTable(CreateTableEnhancedRequest.builder().build());
            } catch (ResourceInUseException e) {
                log.info("A tabela {} já existe. Não foi necessário cria-lá", TABLE_NAME);
            }
            return table;
        }
        // Adicione mais métodos com Bean conforme o metodo acima para criar tabelas conforme necessário
    }
}
