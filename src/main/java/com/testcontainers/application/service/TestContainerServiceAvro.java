package com.testcontainers.application.service;

import com.testcontainers.adapters.persistence.dao.EmployeeDao;
import com.testcontainers.adapters.producer.TestProducer;
import com.testcontainers.avro.ModeloAvro1;
import com.testcontainers.domain.entities.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TestContainerServiceAvro {


    private final TestProducer producer;
    private final EmployeeDao employeeDao;

    public void execute(ModeloAvro1 employee) {

        Optional<Employee> employeeOptional = employeeDao.findById(Integer.valueOf(employee.getId()));

        ModeloAvro1 employee2 = ModeloAvro1.newBuilder()
                .setId(String.valueOf(employeeOptional.map(Employee::getId).orElse(99)))
                .setDescricao(employeeOptional.map(Employee::getFirstName).orElse("Sem nome"))
                .build();

        producer.send(employee2, "employee_Topico");
    }
}
