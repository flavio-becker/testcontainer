package com.testcontainers.adapters.listeners;

import com.testcontainers.application.service.TestContainerServiceAvro;
import com.testcontainers.avro.ModeloAvro1;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsumerAvro {

    private final TestContainerServiceAvro containerService;

    @KafkaListener(topics = "topico", groupId = "demo", containerFactory = "listenerContainerFactoryBean")
        public void handle(ModeloAvro1 modeloAvro1) {


        containerService.execute(modeloAvro1);

    }
}
