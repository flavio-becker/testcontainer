//package com.testcontainers.adapters.listeners;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.avro.generic.GenericRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.util.List;
//
//@Profile("test")
//@Component
//@RequiredArgsConstructor
//public class KafkaTestConsumer {
//
//    private final KafkaConsumer<String, GenericRecord> consumer;
//
////    public KafkaTestConsumer(String bootstrapServers, String groupId) {
////        Properties props = new Properties();
////
////        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
////        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
////        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
////        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
////        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
////        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
////
////        this.consumer = new KafkaConsumer<>(props);
////    }
//
//    public void subscribe(List<String> topics) {
//        consumer.subscribe(topics);
//    }
//
//    public ConsumerRecords<String, GenericRecord> poll() {
//        return consumer.poll(Duration.ofSeconds(10));
//    }
//}
