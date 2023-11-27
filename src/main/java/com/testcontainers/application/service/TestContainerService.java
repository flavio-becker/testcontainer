//package com.testcontainers.application.service;
//
//import com.testcontainers.adapters.producer.TestProducer;
//import com.testcontainers.domain.entities.Employee;
//import com.testcontainers.domain.entities.EmployeeEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//public class TestContainerService {
//
//    private final TestcontRepository repository;
//    private final TestProducer producer;
//
//    public void execute(EmployeeEvent employee) {
//
//        Employee employee1 = Employee.builder()
//                .firstName(employee.firstName())
//                .lastName(employee.lastName())
//                .build();
//
//        Employee employeeSaved = repository.saveAndFlush(employee1);
//
//        EmployeeEvent event = new EmployeeEvent(employeeSaved.getId(), employeeSaved.getFirstName(), employeeSaved.getLastName());
//
//        producer.send(event);
//    }
//}
