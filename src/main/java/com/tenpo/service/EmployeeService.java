package com.tenpo.service;

import com.tenpo.api.dto.EmployeeRequestDTO;
import com.tenpo.api.dto.EmployeeResponseDTO;
import com.tenpo.model.Client;
import com.tenpo.model.Employee;
import com.tenpo.repository.ClientRepository;
import com.tenpo.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Employee employee = Employee.builder()
                .name(request.name())
                .rut(request.rut())
                .client(client)
                .build();

        employee = employeeRepository.save(employee);
        return mapToResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(employee);
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Integer id, EmployeeRequestDTO request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        employee.setName(request.name());
        employee.setRut(request.rut());
        employee.setClient(client);

        employee = employeeRepository.save(employee);
        return mapToResponse(employee);
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    private EmployeeResponseDTO mapToResponse(@org.springframework.lang.NonNull Employee employee) {
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .rut(employee.getRut())
                .clientId(employee.getClient().getId())
                .build();
    }
}
