package com.testcontainers;

import com.testcontainers.adapters.persistence.dao.EmployeeDao;
import com.testcontainers.awstestcontainers.EnableAWSTestcontainers;
import com.testcontainers.domain.entities.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAWSTestcontainers
@ActiveProfiles({"test"})
public class DynamoDBTest {

    @Autowired
    private EmployeeDao employeeDao;

//    @BeforeEach
//    public void setUp() {
//        employeeDao.deleteAll();
//    }


    @Test
    void saveNewElementTest() {
        var employeeOne = Employee.builder()
                .id(null)
                .firstName("Vicenzo")
                .lastName("Racca")
                .build();

        employeeDao.save(employeeOne);

        List<Employee> retrievedEmployees = employeeDao.findAll();
        Assertions.assertThat(retrievedEmployees).hasSize(1);
        Assertions.assertThat(retrievedEmployees.get(0)).isEqualTo(employeeOne);
    }

    @Test
    void saveAnExistingElementTest() {
        var employeeOne = Employee.builder()
                .id(null)
                .firstName("Vicenzo")
                .lastName("Racca")
                .build();

        employeeDao.save(employeeOne);

        List<Employee> retrievedEmployees = employeeDao.findAll();
        Assertions.assertThat(retrievedEmployees).hasSize(1);
        Assertions.assertThat(retrievedEmployees.get(0)).isEqualTo(employeeOne);
        Assertions.assertThat(retrievedEmployees.get(0).getFirstName()).isEqualTo("Vincenzo");

        Employee employeeTwo = employeeOne.withFirstName("Enzo");


        employeeDao.save(employeeTwo);

        retrievedEmployees = employeeDao.findAll();
        assertThat(retrievedEmployees).hasSize(1);
        assertThat(retrievedEmployees.get(0)).isEqualTo(employeeOne);
        assertThat(retrievedEmployees.get(0).getFirstName()).isEqualTo("Enzo");
    }

    @Test
    void deleteTest() {
        var employeeOne = Employee.builder()
                .id(null)
                .firstName("Vicenzo")
                .lastName("Racca")
                .build();

        employeeDao.save(employeeOne);
        Integer id = employeeOne.getId();
        assertThat(id).isNotNull();

        Optional<Employee> retrievedUser = employeeDao.findById(id);
        Assertions.assertThat(retrievedUser.isPresent()).isTrue();
        Assertions.assertThat(retrievedUser.get()).isEqualTo(employeeOne);

        employeeDao.delete(employeeOne);
        Optional<Employee> userNotRetrieved = employeeDao.findById(id);
        Assertions.assertThat(userNotRetrieved.isPresent()).isFalse();

    }
}
