//package com.testcontainers.awstestcontainers;
//
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.localstack.LocalStackContainer;
//import org.testcontainers.containers.wait.strategy.Wait;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;
//import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;
//
//@Testcontainers
//public class LocalStackTestcontainers {
//
////    @Autowired
////    AmazonDynamoDB amazonDynamoDB;
//
//    @Container
//    static LocalStackContainer localStack =
//            new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.0.0"))
//                    .withServices(DYNAMODB, SQS)
//                    .withExposedPorts(4566)
//                    .waitingFor(Wait.forLogMessage(".*Running on.*", 1));
//
//    static {
//        localStack.start();
//    }
//
//    @DynamicPropertySource
//    static void overrideProperties(DynamicPropertyRegistry registry) {
//        registry.add("app.queue", () -> "fila");
//        registry.add(
//                "spring.cloud.aws.region.static",
//                () -> localStack.getRegion()
//        );
//        registry.add(
//                "spring.cloud.aws.credentials.access-key",
//                () -> localStack.getAccessKey()
//        );
//        registry.add(
//                "spring.cloud.aws.credentials.secret-key",
//                () -> localStack.getSecretKey()
//        );
//        registry.add(
//                "spring.cloud.aws.sqs.endpoint",
//                () -> localStack.getEndpointOverride(SQS).toString()
//        );
//    }
//
////    @PostConstruct
////    public void init() {
////        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
////
////        CreateTableRequest tableUserRequest = dynamoDBMapper
////                .generateCreateTableRequest(Employee.class);
////
////        tableUserRequest.setProvisionedThroughput(
////                new ProvisionedThroughput(1L, 1L));
////
////        try{
////            amazonDynamoDB.createTable(tableUserRequest);
////        } catch (ResourceInUseException ex) {
////            System.out.println("Tabela ja criada previamente!!");
////        }
////       }
//}
