package com.testcontainers.adapters.sqs;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final QueueMessagingTemplate sqsTemplate;

    public void publish(String queueName, Message message) {
        sqsTemplate.convertAndSend(queueName, message);
    }
}
