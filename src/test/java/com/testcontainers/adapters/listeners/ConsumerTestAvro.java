package com.testcontainers.adapters.listeners;

import com.testcontainers.adapters.producer.TestProducer;
import com.testcontainers.avro.ModeloAvro1;
import com.testcontainers.kafkatestcontainers.EnableKafkaTestcontainers;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.testcontainers.kafkatestcontainers.KafkaTestcontainersInitializer.KAFKA;
import static com.testcontainers.kafkatestcontainers.KafkaTestcontainersInitializer.SCHEMA_REGISTRY;
import static java.util.Collections.singletonList;

@SpringBootTest
@EnableKafkaTestcontainers
@ActiveProfiles({"test"})
class ConsumerTestAvro {

    @Autowired
    TestProducer kafkaProducer;

    @Test
    void shouldHandleProductPriceChangedEvent() {

        ModeloAvro1 modeloAvro1 = ModeloAvro1.newBuilder()
                .setId("1")
                .setDescricao("TESTE_AVRO")
                .build();

        kafkaProducer.send(modeloAvro1, "topico");

        KafkaTestConsumerAvro kafkaTestConsumer =
                new KafkaTestConsumerAvro(KAFKA.getBootstrapServers(), "demo_test", SCHEMA_REGISTRY.getSchemaUrl());

        kafkaTestConsumer.subscribe(singletonList("employee_Topico"));

        ConsumerRecords<String, GenericRecord> records = kafkaTestConsumer.poll();

        Assertions.assertThat(records.count())
                .isEqualTo(1);

        // As mentioned before, in this example we are using String serializers, in practice it is very common to use Avro schemas to serialize these messages
//        records.iterator().forEachRemaining(record -> Assertions.assertThat(record.value())
//                .isEqualTo(EmployeeEvent.builder()
//                        .firstName("Flavio")
//                        .lastName("Becker")
//                        .build()));

    }
}