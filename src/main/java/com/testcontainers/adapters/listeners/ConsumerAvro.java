package com.testcontainers.adapters.listeners;

import com.testcontainers.application.service.TestContainerServiceAvro;
import com.testcontainers.avro.ModeloAvro1;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@KafkaListener(topics = "topico", containerFactory = "listenerContainerFactoryBean")
public class ConsumerAvro {

    private final TestContainerServiceAvro containerService;

    @KafkaHandler
    public void handle(@Payload ModeloAvro1 modeloAvro1, @Headers MessageHeaders headers, final Acknowledgment ack) {


        containerService.execute(modeloAvro1);

    }

    private void logaInformacao(MessageHeaders headers) {

        logaInformacao(headers);
    }
}
