//package com.testcontainers.adapters.listeners;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.testcontainers.domain.entities.EmployeeEvent;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.TestPropertySource;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import static java.util.Collections.singletonList;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(
//        properties = {
//                "spring.kafka.consumer.auto-offset-reset=earliest",
//        }
//)
//@Testcontainers
//class ConsumerTest {
//
//    @Container
//    static final KafkaContainer kafka = new KafkaContainer(
//            DockerImageName.parse("confluentinc/cp-kafka:7.3.3")
//    );
//
//    @Container
//    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
//            .withDatabaseName("testcontainerdb").withUsername("test").withPassword("test")
//            .withInitScript("schema_Cassandra.sql");
//
//    @DynamicPropertySource
//    static void overrideProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//
//        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
//        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
//        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
//    }
//
//    @Autowired
//    private KafkaTemplate<String, EmployeeEvent> kafkaTemplate;
//
//
//    @Test
//    void shouldHandleProductPriceChangedEvent() {
//
//        EmployeeEvent employee = new EmployeeEvent(null,"Flavio", "Becker");
//
//        kafkaTemplate.send("topico", "123456789", employee);
//
//        KafkaTestConsumer kafkaTestConsumer = new KafkaTestConsumer(kafka.getBootstrapServers(), "demo_test");
//
//        kafkaTestConsumer.subscribe(singletonList("employee_Topico"));
//
//        ConsumerRecords<String, EmployeeEvent> records = kafkaTestConsumer.poll();
//
//        Assertions.assertThat(records.count())
//                .isEqualTo(1);
//
//        // As mentioned before, in this example we are using String serializers, in practice it is very common to use Avro schemas to serialize these messages
//        records.iterator().forEachRemaining(record -> Assertions.assertThat(record.value())
//                .isEqualTo(EmployeeEvent.builder()
//                        .firstName("Flavio")
//                        .lastName("Becker")
//                        .build()));
//
//    }
//}