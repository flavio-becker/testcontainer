package com.testcontainers.adapters.sqs;

import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final QueueMessagingTemplate sqsTemplate;

    public void publish(String queueName, Message message) {
        sqsTemplate.convertAndSend(queueName, message);
    }
}
