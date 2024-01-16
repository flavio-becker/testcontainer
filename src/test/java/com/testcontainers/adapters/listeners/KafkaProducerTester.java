package com.testcontainers.adapters.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@TestComponent
@RequiredArgsConstructor
public class KafkaProducerTester {

    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    public void sendMessage(GenericRecord rec, String topic, Map<String, byte[]> headers) {

        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topic, rec);

        headers.forEach((k, value) -> record.headers().add(k, value));

        kafkaTemplate.send(record);
    }
}
