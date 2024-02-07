package com.testcontainers.adapters.listeners;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaTestConsumerAvro<TypeAvro> {

    private final KafkaConsumer<String, TypeAvro> consumer;

    public KafkaTestConsumerAvro(String bootstrapServers, String groupId, String schemaurl) {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Utf8StringAvroDeserializer.class.getName()); // Usando o novo deserializador
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        props.put("schema.registry.url", schemaurl);

        this.consumer = new KafkaConsumer<>(props);
    }

    public void subscribe(List<String> topics) {
        consumer.subscribe(topics);
    }

    public ConsumerRecords<String, TypeAvro> poll() {
        return consumer.poll(Duration.ofSeconds(50));
    }
}

