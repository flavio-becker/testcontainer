package com.testcontainers.adapters.producer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class TestProducer {

    private final KafkaTemplate<String, GenericRecord> producer;

    @SneakyThrows
    public void send(GenericRecord employee, String topico) {

        CompletableFuture<SendResult<String, GenericRecord>> employeeTopico = producer.send(topico, UUID.randomUUID().toString(), employee);

        System.out.println(employeeTopico.get().getRecordMetadata());
    }

}
