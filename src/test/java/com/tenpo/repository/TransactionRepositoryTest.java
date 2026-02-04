package com.tenpo.repository;

import com.tenpo.model.Client;
import com.tenpo.model.Employee;
import com.tenpo.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("TransactionRepository Integration Tests")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client client;
    private Employee employee;

    @BeforeEach
    void setUp() {
        client = Client.builder().name("Tenpo").rut("123-4").build();
        client = entityManager.persistFlushFind(client);

        employee = Employee.builder().name("Worker").rut("55-5").client(client).build();
        employee = entityManager.persistFlushFind(employee);
    }

    @Test
    @DisplayName("Should count transactions by client id")
    void shouldCountByClientId() {
        // Arrange
        entityManager.persist(Transaction.builder().amount(100).merchantOrBusiness("A").date(LocalDateTime.now())
                .employee(employee).build());
        entityManager.persist(Transaction.builder().amount(200).merchantOrBusiness("B").date(LocalDateTime.now())
                .employee(employee).build());
        entityManager.flush();

        // Act
        long count = transactionRepository.countByClientId(client.getId());

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should find transactions by client id")
    void shouldFindByClientId() {
        // Arrange
        entityManager.persist(Transaction.builder().amount(100).merchantOrBusiness("A").date(LocalDateTime.now())
                .employee(employee).build());
        entityManager.flush();

        // Act
        List<Transaction> transactions = transactionRepository.findByClientId(client.getId());

        // Assert
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAmount()).isEqualTo(100);
        // Verify @EntityGraph fetch
        assertThat(transactions.get(0).getEmployee()).isNotNull();
        assertThat(transactions.get(0).getEmployee().getName()).isEqualTo("Worker");
    }

    @Test
    @DisplayName("Should find transactions by employee id")
    void shouldFindByEmployeeId() {
        // Arrange
        entityManager.persist(Transaction.builder().amount(500).merchantOrBusiness("Coffee").date(LocalDateTime.now())
                .employee(employee).build());
        entityManager.flush();

        // Act
        List<Transaction> transactions = transactionRepository.findByEmployeeId(employee.getId());

        // Assert
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getMerchantOrBusiness()).isEqualTo("Coffee");
    }

    @Test
    @DisplayName("Should return 0 when counting transactions for client with none")
    void shouldReturnZeroWhenNoTransactionsForClient() {
        // Act
        long count = transactionRepository.countByClientId(999);

        // Assert
        assertThat(count).isZero();
    }

    @Test
    @DisplayName("Should save and find transaction")
    void shouldSaveAndFindTransaction() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .amount(1000)
                .merchantOrBusiness("Store")
                .date(LocalDateTime.now())
                .employee(employee)
                .build();

        // Act
        Transaction saved = transactionRepository.save(transaction);
        entityManager.flush();
        entityManager.clear();

        Transaction found = transactionRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getAmount()).isEqualTo(1000);
        assertThat(found.getEmployee().getId()).isEqualTo(employee.getId());
    }
}
