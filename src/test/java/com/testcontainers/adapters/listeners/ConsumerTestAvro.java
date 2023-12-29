package com.testcontainers.adapters.listeners;

import com.testcontainers.adapters.persistence.dao.EmployeeDao;
import com.testcontainers.avro.ModeloAvro1;
import com.testcontainers.awstestcontainers.AWSLocalstackTestcontainers;
import com.testcontainers.domain.entities.Employee;
import com.testcontainers.kafkatestcontainers.EnableKafkaTestcontainers;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static com.testcontainers.kafkatestcontainers.KafkaTestcontainersInitializer.KAFKA;
import static com.testcontainers.kafkatestcontainers.KafkaTestcontainersInitializer.SCHEMA_REGISTRY;
import static java.util.Collections.singletonList;

@SpringBootTest
@EnableKafkaTestcontainers
@Import({KafkaProducerTester.class, AWSLocalstackTestcontainers.class})
@ActiveProfiles("test")
@SpringJUnitConfig
class ConsumerTestAvro {


    @Autowired
    KafkaProducerTester kafkaProducerTester;
    @Autowired
    private EmployeeDao employeeDao;

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
        kafkaProducerTester.sendMessage(modeloAvro1, "topico");

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
    }
}