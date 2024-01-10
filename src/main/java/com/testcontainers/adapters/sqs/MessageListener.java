package com.testcontainers.adapters.sqs;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class MessageListener {

    @SqsListener("${app.queue}")
    public void handle(@Payload MessageValor message) {

        String key = message.uuid().toString();

        ByteArrayInputStream is = new ByteArrayInputStream(
                message.content().getBytes(StandardCharsets.UTF_8)
        );
    }
}
