package com.tenpo.service;

import com.tenpo.api.dto.EmployeeRequestDTO;
import com.tenpo.api.dto.EmployeeResponseDTO;
import com.tenpo.model.Client;
import com.tenpo.model.Employee;
import com.tenpo.repository.ClientRepository;
import com.tenpo.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("Should create employee when request and client are valid")
    void shouldCreateEmployeeWhenRequestAndClientAreValid() {
        // Arrange
        Integer clientId = 1;
        EmployeeRequestDTO request = new EmployeeRequestDTO("John Doe", "12.345.678-9", clientId);
        Client client = Client.builder().id(clientId).name("Test Client").build();
        Employee employee = Employee.builder()
                .id(101)
                .name("John Doe")
                .rut("12.345.678-9")
                .client(client)
                .build();

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        EmployeeResponseDTO response = employeeService.createEmployee(request);

        // Assert
        assertNotNull(response);
        assertEquals(101, response.id());
        assertEquals("John Doe", response.name());
        assertEquals(clientId, response.clientId());
        verify(clientRepository, times(1)).findById(clientId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw exception when creating employee with non-existent client")
    void shouldThrowExceptionWhenCreatingEmployeeWithNonExistentClient() {
        // Arrange
        Integer clientId = 99;
        EmployeeRequestDTO request = new EmployeeRequestDTO("John Doe", "12.345.678-9", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(request));
        verify(clientRepository, times(1)).findById(clientId);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    @DisplayName("Should return all employees")
    void shouldReturnAllEmployees() {
        // Arrange
        Client client = Client.builder().id(1).build();
        List<Employee> employees = List.of(
                Employee.builder().id(1).name("Emp 1").client(client).build(),
                Employee.builder().id(2).name("Emp 2").client(client).build());

        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<EmployeeResponseDTO> response = employeeService.getAllEmployees();

        // Assert
        assertEquals(2, response.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return employee when id exists")
    void shouldReturnEmployeeWhenIdExists() {
        // Arrange
        Integer id = 101;
        Client client = Client.builder().id(1).build();
        Employee employee = Employee.builder().id(id).name("John Doe").client(client).build();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponseDTO response = employeeService.getEmployeeById(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.id());
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when employee id does not exist")
    void shouldThrowExceptionWhenEmployeeIdDoesNotExist() {
        // Arrange
        Integer id = 999;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(id));
    }

    @Test
    @DisplayName("Should update employee details when valid")
    void shouldUpdateEmployeeWhenValid() {
        // Arrange
        Integer empId = 101;
        Integer clientId = 1;
        EmployeeRequestDTO request = new EmployeeRequestDTO("Jane Doe", "98.765.432-1", clientId);
        Client client = Client.builder().id(clientId).build();
        Employee existingEmployee = Employee.builder().id(empId).name("Old Name").client(client).build();
        Employee updatedEmployee = Employee.builder().id(empId).name("Jane Doe").rut("98.765.432-1").client(client)
                .build();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(existingEmployee));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        EmployeeResponseDTO response = employeeService.updateEmployee(empId, request);

        // Assert
        assertNotNull(response);
        assertEquals("Jane Doe", response.name());
        verify(employeeRepository, times(1)).findById(empId);
        verify(clientRepository, times(1)).findById(clientId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should delete employee by id")
    void shouldDeleteEmployeeById() {
        // Arrange
        Integer id = 101;
        doNothing().when(employeeRepository).deleteById(id);

        // Act
        employeeService.deleteEmployee(id);

        // Assert
        verify(employeeRepository, times(1)).deleteById(id);
    }
}
