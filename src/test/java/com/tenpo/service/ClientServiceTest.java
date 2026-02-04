package com.tenpo.service;

import com.tenpo.api.dto.ClientRequestDTO;
import com.tenpo.api.dto.ClientResponseDTO;
import com.tenpo.model.Client;
import com.tenpo.repository.ClientRepository;
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
@DisplayName("ClientService Unit Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    @DisplayName("Should create client when request is valid")
    void shouldCreateClientWhenRequestIsValid() {
        // Arrange
        ClientRequestDTO request = new ClientRequestDTO("Google", "12345678-9");
        Client client = Client.builder()
                .id(1)
                .name("Google")
                .rut("12345678-9")
                .build();

        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Act
        ClientResponseDTO response = clientService.createClient(request);

        // Assert
        assertNotNull(response);
        assertEquals(client.getId(), response.id());
        assertEquals(request.name(), response.name());
        assertEquals(request.rut(), response.rut());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Should return all clients")
    void shouldReturnAllClients() {
        // Arrange
        List<Client> clients = List.of(
                Client.builder().id(1).name("Client A").rut("1-9").build(),
                Client.builder().id(2).name("Client B").rut("2-9").build());

        when(clientRepository.findAll()).thenReturn(clients);

        // Act
        List<ClientResponseDTO> response = clientService.getAllClients();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Client A", response.get(0).name());
        assertEquals("Client B", response.get(1).name());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no clients exist")
    void shouldReturnEmptyListWhenNoClientsExist() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(List.of());

        // Act
        List<ClientResponseDTO> response = clientService.getAllClients();

        // Assert
        assertTrue(response.isEmpty());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return client when id exists")
    void shouldReturnClientWhenIdExists() {
        // Arrange
        Integer id = 1;
        Client client = Client.builder().id(id).name("Test Corp").rut("11-2").build();

        when(clientRepository.findById(id)).thenReturn(Optional.of(client));

        // Act
        ClientResponseDTO response = clientService.getClientById(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals("Test Corp", response.name());
        verify(clientRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when client id does not exist")
    void shouldThrowExceptionWhenClientIdDoesNotExist() {
        // Arrange
        Integer id = 99;
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> clientService.getClientById(id));
        verify(clientRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should update client when id exists")
    void shouldUpdateClientWhenIdExists() {
        // Arrange
        Integer id = 1;
        ClientRequestDTO request = new ClientRequestDTO("Updated Name", "99-9");
        Client existingClient = Client.builder().id(id).name("Old Name").rut("00-0").build();
        Client updatedClient = Client.builder().id(id).name("Updated Name").rut("99-9").build();

        when(clientRepository.findById(id)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

        // Act
        ClientResponseDTO response = clientService.updateClient(id, request);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Name", response.name());
        assertEquals("99-9", response.rut());
        verify(clientRepository, times(1)).findById(id);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Should delete client by id")
    void shouldDeleteClientById() {
        // Arrange
        Integer id = 1;
        doNothing().when(clientRepository).deleteById(id);

        // Act
        clientService.deleteClient(id);

        // Assert
        verify(clientRepository, times(1)).deleteById(id);
    }
}
