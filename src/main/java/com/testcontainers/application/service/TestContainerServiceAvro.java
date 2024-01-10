package com.testcontainers.application.service;

import com.testcontainers.adapters.persistence.dao.EmployeeDao;
import com.testcontainers.adapters.producer.TestProducer;
import com.testcontainers.adapters.sqs.MessageSender;
import com.testcontainers.adapters.sqs.MessageValor;
import com.testcontainers.avro.ModeloAvro1;
import com.testcontainers.domain.entities.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class TestContainerServiceAvro {


    private final TestProducer producer;
    private final EmployeeDao employeeDao;
    private final MessageSender messageSender;

    public void execute(ModeloAvro1 employee) {

        Optional<Employee> employeeOptional = employeeDao.findById(employee.getId());


        String saldo;
        if (employeeOptional.map(Employee::getFirstname).get().equalsIgnoreCase("Flavio") ) {
            saldo = "1000";
        } else if (employeeOptional.map(Employee::getFirstname).get().equalsIgnoreCase("Jose")) {
            saldo = "2000";
        } else if (employeeOptional.map(Employee::getFirstname).get().equalsIgnoreCase("Maria")) {
            saldo = "10000";
        } else {
            saldo = "0";
        }

        employeeDao.save(employeeOptional.get()
                .withSaldo(saldo));

        ModeloAvro1 employee2 = ModeloAvro1.newBuilder()
                .setId(String.valueOf(employeeOptional.map(Employee::getId).orElse(String.valueOf(UUID.randomUUID()))))
                .setDescricao(employeeOptional.map(Employee::getFirstname).orElse("Sem nome"))
                .build();

        MessageValor messageValor = new MessageValor(UUID.randomUUID(), employee2.getDescricao());
        messageSender.publish("fila", messageValor);
        producer.send(employee2, "employee_Topico");
    }
}
