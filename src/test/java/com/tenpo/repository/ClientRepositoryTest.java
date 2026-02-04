package com.tenpo.repository;

import com.tenpo.model.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ClientRepository Integration Tests")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save client when valid")
    void shouldSaveClientWhenValid() {
        // Arrange
        Client client = Client.builder()
                .name("Microsoft")
                .rut("123-K")
                .build();

        // Act
        Client savedClient = clientRepository.save(client);

        // Assert
        assertThat(savedClient).isNotNull();
        assertThat(savedClient.getId()).isPositive();
        assertThat(savedClient.getName()).isEqualTo("Microsoft");
    }

    @Test
    @DisplayName("Should find client by id when exists")
    void shouldFindClientByIdWhenExists() {
        // Arrange
        Client client = Client.builder().name("Apple").rut("456-7").build();
        client = entityManager.persistFlushFind(client);

        // Act
        Optional<Client> foundClient = clientRepository.findById(client.getId());

        // Assert
        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("Should find all clients")
    void shouldFindAllClients() {
        // Arrange
        entityManager.persist(Client.builder().name("A").rut("1").build());
        entityManager.persist(Client.builder().name("B").rut("2").build());
        entityManager.flush();

        // Act
        List<Client> clients = clientRepository.findAll();

        // Assert
        assertThat(clients).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should update client name when valid")
    void shouldUpdateClientWhenValid() {
        // Arrange
        Client client = Client.builder().name("Old Name").rut("999").build();
        client = entityManager.persistFlushFind(client);
        client.setName("New Name");

        // Act
        Client updatedClient = clientRepository.save(client);
        entityManager.flush();

        // Assert
        Client found = entityManager.find(Client.class, updatedClient.getId());
        assertThat(found.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should delete client when exists")
    void shouldDeleteClientWhenExists() {
        // Arrange
        Client client = Client.builder().name("To Delete").rut("000").build();
        client = entityManager.persistFlushFind(client);

        // Act
        clientRepository.deleteById(client.getId());
        entityManager.flush();

        // Assert
        Client found = entityManager.find(Client.class, client.getId());
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Should return empty optional when findById non-existent id")
    void shouldReturnEmptyWhenIdDoesNotExist() {
        // Act
        Optional<Client> found = clientRepository.findById(9999);

        // Assert
        assertThat(found).isEmpty();
    }
}
