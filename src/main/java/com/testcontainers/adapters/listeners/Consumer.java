//package com.testcontainers.adapters.listeners;
//
//import com.testcontainers.application.service.TestContainerService;
//import com.testcontainers.domain.entities.EmployeeEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//public class Consumer {
//
//    private final TestContainerService containerService;
//
//    @KafkaListener(topics = "topico", groupId = "demo")
//        public void handle(EmployeeEvent employee) {
//
//        containerService.execute(employee);
//
//    }
//}
