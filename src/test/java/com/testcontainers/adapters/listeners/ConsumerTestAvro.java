package com.testcontainers.adapters.listeners;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.testcontainers.adapters.persistence.dao.EmployeeDao;
import com.testcontainers.avro.ModeloAvro1;
import com.testcontainers.awstestcontainers.AWSLocalstackTestcontainers;
import com.testcontainers.domain.entities.Employee;
import com.testcontainers.kafkatestcontainers.EnableKafkaTestcontainers;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.testcontainers.kafkatestcontainers.KafkaTestcontainersInitializer.KAFKA;
import static com.testcontainers.kafkatestcontainers.KafkaTestcontainersInitializer.SCHEMA_REGISTRY;
import static java.util.Collections.singletonList;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@SpringBootTest
@EnableKafkaTestcontainers
@Import({KafkaProducerTester.class/*, AWSLocalstackTestcontainers.class*/})
@ActiveProfiles("test")
@SpringJUnitConfig
class ConsumerTestAvro extends AWSLocalstackTestcontainers {


    @Autowired
    KafkaProducerTester kafkaProducerTester;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    @Test
    void shouldHandleProductPriceChangedEvent() {

        //GIVEN
        final String ID = "9999";
        String FIRSTNAME = "Flavio";

        //preparando o Dynamo com dados para o teste
        Employee employeeSavedynamo = Employee.builder()
                .id(ID)
                .firstname(FIRSTNAME)
                .build();

        employeeDao.save(employeeSavedynamo);

        ModeloAvro1 modeloAvro1 = ModeloAvro1.newBuilder()
                .setId(ID)
                .setDescricao("")
                .build();

        //WHEN
        kafkaProducerTester.sendMessage(modeloAvro1, "topico", getHeaders());

        //THEN
        KafkaTestConsumerAvro kafkaTestConsumer =
                new KafkaTestConsumerAvro(KAFKA.getBootstrapServers(), "test", SCHEMA_REGISTRY.getSchemaUrl());

        kafkaTestConsumer.subscribe(singletonList("employee_Topico"));

        ConsumerRecords<String, GenericRecord> records = kafkaTestConsumer.poll();

        Assertions.assertThat(records.count())
                .isEqualTo(1);


        records.iterator().forEachRemaining(record -> {
            if (record.value() instanceof ModeloAvro1 avro1) {
                Assertions.assertThat(avro1.getDescricao())
                        .isEqualTo(FIRSTNAME);
            }
        });

        // Verifica a mensagem no SQS
        String sqsQueueUrl = localStackContainer.getEndpointOverride(SQS).toString() + "/queue/fila";
        ;

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsQueueUrl)
                .withMaxNumberOfMessages(1)
                .withWaitTimeSeconds(5);

        List<Message> messages = amazonSQSAsync.receiveMessage(receiveMessageRequest).getMessages();
        Assertions.assertThat(messages).hasSize(1);
    }

    public Map<String, byte[]> getHeaders() {

        Map<String, byte[]> headers = new HashMap<>();
        headers.put("source", "HeadersTeste".getBytes(StandardCharsets.UTF_8));
        
        return headers;
    }

}