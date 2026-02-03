package com.tenpo.service;

import com.tenpo.api.dto.TransactionRequestDTO;
import com.tenpo.api.dto.TransactionResponseDTO;
import com.tenpo.model.Employee;
import com.tenpo.model.Transaction;
import com.tenpo.repository.EmployeeRepository;
import com.tenpo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getClient().getId() != request.clientId()) {
            throw new RuntimeException("Employee does not belong to the client");
        }

        // Constraint: Max 100 transactions per client
        long transactionCount = transactionRepository.countByClientId(employee.getClient().getId());
        if (transactionCount >= 100) {
            throw new RuntimeException("Client has reached the maximum of 100 transactions");
        }

        // Business rules from requirements:
        // - No negative amounts (handled by @Min in DTO)
        // - No future dates (handled by @PastOrPresent in DTO)

        Transaction transaction = Transaction.builder()
                .amount(request.amount())
                .merchantOrBusiness(request.merchantOrBusiness())
                .date(request.date())
                .employee(employee)
                .build();

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByClientId(Integer clientId) {
        return transactionRepository.findByClientId(clientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(Integer id, TransactionRequestDTO request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        transaction.setAmount(request.amount());
        transaction.setMerchantOrBusiness(request.merchantOrBusiness());
        transaction.setDate(request.date());
        transaction.setEmployee(employee);

        transaction = transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(Integer id) {
        transactionRepository.deleteById(id);
    }

    private TransactionResponseDTO mapToResponse(@org.springframework.lang.NonNull Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .merchantOrBusiness(transaction.getMerchantOrBusiness())
                .date(transaction.getDate())
                .employeeId(transaction.getEmployee().getId())
                .build();
    }
}
