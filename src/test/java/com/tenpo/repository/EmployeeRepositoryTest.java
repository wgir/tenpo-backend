package com.tenpo.repository;

import com.tenpo.model.Client;
import com.tenpo.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("EmployeeRepository Integration Tests")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save employee when valid")
    void shouldSaveEmployeeWhenValid() {
        // Arrange
        Client client = Client.builder().name("Test Client").rut("1-1").build();
        client = entityManager.persistFlushFind(client);

        Employee employee = Employee.builder()
                .name("John Smith")
                .rut("12.345.678-9")
                .client(client)
                .build();

        // Act
        Employee savedEmployee = employeeRepository.save(employee);

        // Assert
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isNotNull();
        assertThat(savedEmployee.getClient().getId()).isEqualTo(client.getId());
    }

    @Test
    @DisplayName("Should find employee by rut when exists")
    void shouldFindEmployeeByRutWhenExists() {
        // Arrange
        Client client = Client.builder().name("Test Client").rut("1-2").build();
        client = entityManager.persistFlushFind(client);

        Employee employee = Employee.builder()
                .name("Alice")
                .rut("99.999.999-9")
                .client(client)
                .build();
        entityManager.persistAndFlush(employee);

        // Act
        Optional<Employee> found = employeeRepository.findByRut("99.999.999-9");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
        // Verify @EntityGraph fetch (client should be loaded)
        assertThat(found.get().getClient()).isNotNull();
        assertThat(found.get().getClient().getName()).isEqualTo("Test Client");
    }

    @Test
    @DisplayName("Should return empty when findByRut non-existent rut")
    void shouldReturnEmptyWhenRutDoesNotExist() {
        // Act
        Optional<Employee> found = employeeRepository.findByRut("00.000.000-0");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should delete employee but keep client")
    void shouldDeleteEmployeeButKeepClient() {
        // Arrange
        Client client = Client.builder().name("Stay").rut("S-1").build();
        client = entityManager.persistFlushFind(client);

        Employee employee = Employee.builder().name("Go").rut("G-1").client(client).build();
        employee = entityManager.persistFlushFind(employee);

        // Act
        employeeRepository.deleteById(employee.getId());
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(employeeRepository.findById(employee.getId())).isEmpty();
        assertThat(entityManager.find(Client.class, client.getId())).isNotNull();
    }
}
