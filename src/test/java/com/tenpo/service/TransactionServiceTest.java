package com.tenpo.service;

import com.tenpo.api.dto.TransactionRequestDTO;
import com.tenpo.api.dto.TransactionResponseDTO;
import com.tenpo.model.Client;
import com.tenpo.model.Employee;
import com.tenpo.model.Transaction;
import com.tenpo.repository.EmployeeRepository;
import com.tenpo.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Should create transaction when request is valid and constraints are met")
    void shouldCreateTransactionWhenRequestIsValid() {
        // Arrange
        Integer clientId = 1;
        Integer employeeId = 101;
        LocalDateTime now = LocalDateTime.now();
        TransactionRequestDTO request = new TransactionRequestDTO(500, "Starbucks", now, employeeId, clientId);

        Client client = Client.builder().id(clientId).build();
        Employee employee = Employee.builder().id(employeeId).client(client).build();
        Transaction transaction = Transaction.builder()
                .id(1001)
                .amount(500)
                .merchantOrBusiness("Starbucks")
                .date(now)
                .employee(employee)
                .build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(transactionRepository.countByClientId(clientId)).thenReturn(50L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        TransactionResponseDTO response = transactionService.createTransaction(request);

        // Assert
        assertNotNull(response);
        assertEquals(1001, response.id());
        assertEquals(500, response.amount());
        verify(transactionRepository, times(1)).countByClientId(clientId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when employee does not belong to client")
    void shouldThrowExceptionWhenEmployeeDoesNotBelongToClient() {
        // Arrange
        Integer requestClientId = 1;
        Integer actualClientId = 2;
        Integer employeeId = 101;
        TransactionRequestDTO request = new TransactionRequestDTO(500, "Biz", LocalDateTime.now(), employeeId,
                requestClientId);

        Client client = Client.builder().id(actualClientId).build();
        Employee employee = Employee.builder().id(employeeId).client(client).build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.createTransaction(request));
        assertEquals("Employee does not belong to the client", exception.getMessage());
        verifyNoInteractions(transactionRepository);
    }

    @Test
    @DisplayName("Should throw exception when client reaches maximum transaction limit")
    void shouldThrowExceptionWhenMaxTransactionsReached() {
        // Arrange
        Integer clientId = 1;
        Integer employeeId = 101;
        TransactionRequestDTO request = new TransactionRequestDTO(500, "Biz", LocalDateTime.now(), employeeId,
                clientId);

        Client client = Client.builder().id(clientId).build();
        Employee employee = Employee.builder().id(employeeId).client(client).build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(transactionRepository.countByClientId(clientId)).thenReturn(100L);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> transactionService.createTransaction(request));
        assertEquals("Client has reached the maximum of 100 transactions", exception.getMessage());
        verify(transactionRepository, times(1)).countByClientId(clientId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should return transactions by client id")
    void shouldReturnTransactionsByClientId() {
        // Arrange
        Integer clientId = 1;
        Client client = Client.builder().id(clientId).build();
        Employee employee = Employee.builder().id(101).client(client).build();
        List<Transaction> transactions = List.of(
                Transaction.builder().id(1).amount(100).employee(employee).build(),
                Transaction.builder().id(2).amount(200).employee(employee).build());

        when(transactionRepository.findByClientId(clientId)).thenReturn(transactions);

        // Act
        List<TransactionResponseDTO> response = transactionService.getTransactionsByClientId(clientId);

        // Assert
        assertEquals(2, response.size());
        verify(transactionRepository, times(1)).findByClientId(clientId);
    }

    @Test
    @DisplayName("Should return transaction when id exists")
    void shouldReturnTransactionWhenIdExists() {
        // Arrange
        Integer id = 1001;
        Client client = Client.builder().id(1).build();
        Employee employee = Employee.builder().id(101).client(client).build();
        Transaction transaction = Transaction.builder().id(id).amount(500).employee(employee).build();

        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        // Act
        TransactionResponseDTO response = transactionService.getTransactionById(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.id());
        verify(transactionRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when transaction id does not exist")
    void shouldThrowExceptionWhenTransactionIdDoesNotExist() {
        // Arrange
        Integer id = 9999;
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> transactionService.getTransactionById(id));
    }

    @Test
    @DisplayName("Should update transaction when valid")
    void shouldUpdateTransactionWhenValid() {
        // Arrange
        Integer transId = 1001;
        Integer empId = 101;
        Integer clientId = 1;
        LocalDateTime now = LocalDateTime.now();
        TransactionRequestDTO request = new TransactionRequestDTO(600, "Updated Store", now, empId, clientId);

        Client client = Client.builder().id(clientId).build();
        Employee employee = Employee.builder().id(empId).client(client).build();
        Transaction existingTransaction = Transaction.builder().id(transId).amount(500).employee(employee).build();
        Transaction updatedTransaction = Transaction.builder().id(transId).amount(600)
                .merchantOrBusiness("Updated Store").date(now).employee(employee).build();

        when(transactionRepository.findById(transId)).thenReturn(Optional.of(existingTransaction));
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // Act
        TransactionResponseDTO response = transactionService.updateTransaction(transId, request);

        // Assert
        assertNotNull(response);
        assertEquals(600, response.amount());
        assertEquals("Updated Store", response.merchantOrBusiness());
        verify(transactionRepository, times(1)).findById(transId);
        verify(employeeRepository, times(1)).findById(empId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should delete transaction by id")
    void shouldDeleteTransactionById() {
        // Arrange
        Integer id = 1001;
        doNothing().when(transactionRepository).deleteById(id);

        // Act
        transactionService.deleteTransaction(id);

        // Assert
        verify(transactionRepository, times(1)).deleteById(id);
    }
}
