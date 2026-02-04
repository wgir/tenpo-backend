package com.tenpo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.api.dto.ClientRequestDTO;
import com.tenpo.api.dto.ClientResponseDTO;
import com.tenpo.service.ClientService;
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

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    @Test
    @DisplayName("Should create client when request is valid")
    void shouldCreateClientWhenRequestIsValid() throws Exception {
        // Arrange
        ClientRequestDTO request = new ClientRequestDTO("Tenpo", "12345678-9");
        ClientResponseDTO response = new ClientResponseDTO(1, "Tenpo", "12345678-9");

        when(clientService.createClient(any(ClientRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Tenpo")))
                .andExpect(jsonPath("$.rut", is("12345678-9")));

        verify(clientService, times(1)).createClient(any(ClientRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when create client request is invalid")
    void shouldReturn400WhenCreateClientRequestIsInvalid() throws Exception {
        // Arrange
        ClientRequestDTO invalidRequest = new ClientRequestDTO("", ""); // Blank fields

        // Act & Assert
        mockMvc.perform(post("/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")));

        verify(clientService, never()).createClient(any());
    }

    @Test
    @DisplayName("Should return all clients")
    void shouldReturnAllClients() throws Exception {
        // Arrange
        List<ClientResponseDTO> clients = List.of(
                new ClientResponseDTO(1, "Client 1", "1-1"),
                new ClientResponseDTO(2, "Client 2", "2-2"));

        when(clientService.getAllClients()).thenReturn(clients);

        // Act & Assert
        mockMvc.perform(get("/client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Client 1")))
                .andExpect(jsonPath("$[1].name", is("Client 2")));

        verify(clientService, times(1)).getAllClients();
    }

    @Test
    @DisplayName("Should return client when id exists")
    void shouldReturnClientWhenIdExists() throws Exception {
        // Arrange
        Integer clientId = 1;
        ClientResponseDTO response = new ClientResponseDTO(clientId, "Tenpo", "12345678-9");

        when(clientService.getClientById(clientId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/client/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clientId)))
                .andExpect(jsonPath("$.name", is("Tenpo")));

        verify(clientService, times(1)).getClientById(clientId);
    }

    @Test
    @DisplayName("Should return 400 when client is not found")
    void shouldReturn400WhenClientIsNotFound() throws Exception {
        // Arrange
        Integer clientId = 99;
        when(clientService.getClientById(clientId)).thenThrow(new RuntimeException("Client not found"));

        // Act & Assert
        mockMvc.perform(get("/client/{id}", clientId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Client not found")))
                .andExpect(jsonPath("$.title", is("Business Logic Error")));

        verify(clientService, times(1)).getClientById(clientId);
    }

    @Test
    @DisplayName("Should update client when data is valid")
    void shouldUpdateClientWhenDataIsValid() throws Exception {
        // Arrange
        Integer clientId = 1;
        ClientRequestDTO request = new ClientRequestDTO("Updated Name", "98765432-1");
        ClientResponseDTO response = new ClientResponseDTO(clientId, "Updated Name", "98765432-1");

        when(clientService.updateClient(eq(clientId), any(ClientRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/client/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.rut", is("98765432-1")));

        verify(clientService, times(1)).updateClient(eq(clientId), any(ClientRequestDTO.class));
    }

    @Test
    @DisplayName("Should delete client")
    void shouldDeleteClient() throws Exception {
        // Arrange
        Integer clientId = 1;
        doNothing().when(clientService).deleteClient(clientId);

        // Act & Assert
        mockMvc.perform(delete("/client/{id}", clientId))
                .andExpect(status().isNoContent());

        verify(clientService, times(1)).deleteClient(clientId);
    }

    @Test
    @DisplayName("Should return 500 when server error occurs")
    void shouldReturn500WhenServerErrorOccurs() throws Exception {
        // Arrange
        when(clientService.getAllClients()).thenThrow(new RuntimeException("Database down"));

        // Act & Assert
        mockMvc.perform(get("/client"))
                .andExpect(status().isBadRequest()) // Based on GlobalExceptionHandler mapping RuntimeException to
                                                    // BAD_REQUEST
                .andExpect(jsonPath("$.title", is("Business Logic Error")));
    }
}
