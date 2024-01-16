package com.testcontainers.adapters;

import org.apache.avro.generic.GenericRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

//@Profile("!test")
@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, GenericRecord>>
    listenerContainerFactoryBean(ConsumerFactory<String, GenericRecord> consumerFactory) {

        DefaultKafkaConsumerFactory<String, GenericRecord> consumerFactoryEdit = (DefaultKafkaConsumerFactory<String,GenericRecord>) consumerFactory;

        ConcurrentKafkaListenerContainerFactory<String, GenericRecord> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();

        containerFactory.setConsumerFactory(consumerFactoryEdit);
        containerFactory.setConcurrency(3);
        containerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return containerFactory;
    }
}
