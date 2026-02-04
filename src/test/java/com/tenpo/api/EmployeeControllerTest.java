package com.tenpo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.api.dto.EmployeeRequestDTO;
import com.tenpo.api.dto.EmployeeResponseDTO;
import com.tenpo.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("Should create employee when request is valid")
    void shouldCreateEmployeeWhenRequestIsValid() throws Exception {
        // Arrange
        EmployeeRequestDTO request = new EmployeeRequestDTO("John Doe", "12345678-k", 1);
        EmployeeResponseDTO response = new EmployeeResponseDTO(1, "John Doe", "12345678-k", 1);

        when(employeeService.createEmployee(any(EmployeeRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.rut", is("12345678-k")))
                .andExpect(jsonPath("$.client_id", is(1)));

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when create employee request is invalid")
    void shouldReturn400WhenCreateEmployeeRequestIsInvalid() throws Exception {
        // Arrange
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO("", "", null);

        // Act & Assert
        mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")));

        verify(employeeService, never()).createEmployee(any());
    }

    @Test
    @DisplayName("Should return all employees")
    void shouldReturnAllEmployees() throws Exception {
        // Arrange
        List<EmployeeResponseDTO> employees = List.of(
                new EmployeeResponseDTO(1, "John Doe", "12345678-k", 1),
                new EmployeeResponseDTO(2, "Jane Doe", "87654321-0", 1));

        when(employeeService.getAllEmployees()).thenReturn(employees);

        // Act & Assert
        mockMvc.perform(get("/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    @DisplayName("Should return employee when id exists")
    void shouldReturnEmployeeWhenIdExists() throws Exception {
        // Arrange
        Integer employeeId = 1;
        EmployeeResponseDTO response = new EmployeeResponseDTO(employeeId, "John Doe", "12345678-k", 1);

        when(employeeService.getEmployeeById(employeeId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeeId)))
                .andExpect(jsonPath("$.name", is("John Doe")));

        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    @DisplayName("Should return 400 when employee is not found")
    void shouldReturn400WhenEmployeeIsNotFound() throws Exception {
        // Arrange
        Integer employeeId = 99;
        when(employeeService.getEmployeeById(employeeId)).thenThrow(new RuntimeException("Employee not found"));

        // Act & Assert
        mockMvc.perform(get("/employee/{id}", employeeId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Employee not found")))
                .andExpect(jsonPath("$.title", is("Business Logic Error")));

        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    @DisplayName("Should update employee when data is valid")
    void shouldUpdateEmployeeWhenDataIsValid() throws Exception {
        // Arrange
        Integer employeeId = 1;
        EmployeeRequestDTO request = new EmployeeRequestDTO("Updated Name", "12345678-k", 1);
        EmployeeResponseDTO response = new EmployeeResponseDTO(employeeId, "Updated Name", "12345678-k", 1);

        when(employeeService.updateEmployee(eq(employeeId), any(EmployeeRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/employee/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));

        verify(employeeService, times(1)).updateEmployee(eq(employeeId), any(EmployeeRequestDTO.class));
    }

    @Test
    @DisplayName("Should delete employee")
    void shouldDeleteEmployee() throws Exception {
        // Arrange
        Integer employeeId = 1;
        doNothing().when(employeeService).deleteEmployee(employeeId);

        // Act & Assert
        mockMvc.perform(delete("/employee/{id}", employeeId))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(employeeId);
    }
}
