package com.testcontainers.adapters.listeners;

import lombok.RequiredArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@TestComponent
@RequiredArgsConstructor
public class KafkaProducerTester {

    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    public void sendMessage(GenericRecord rec, String topic) {
        this.kafkaTemplate.send(
                topic, UUID.randomUUID().toString(), rec);
    }
}
