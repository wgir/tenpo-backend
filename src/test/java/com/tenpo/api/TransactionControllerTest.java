package com.tenpo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.api.dto.TransactionRequestDTO;
import com.tenpo.api.dto.TransactionResponseDTO;
import com.tenpo.service.TransactionService;
import com.tenpo.config.WebConfig;
import com.tenpo.config.ContentCachingFilter;
import com.tenpo.interceptor.RateLimitInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import({ RateLimitInterceptor.class, WebConfig.class, ContentCachingFilter.class })
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    @DisplayName("Should create transaction when request is valid")
    void shouldCreateTransactionWhenRequestIsValid() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        TransactionRequestDTO request = new TransactionRequestDTO(100, "Starbucks", now, 1, 1);
        TransactionResponseDTO response = new TransactionResponseDTO(1, 100, "Starbucks", now, 1);

        when(transactionService.createTransaction(any(TransactionRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.amount", is(100)))
                .andExpect(jsonPath("$.merchant_or_business", is("Starbucks")))
                .andExpect(jsonPath("$.employee_id", is(1)));

        verify(transactionService, times(1)).createTransaction(any(TransactionRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when create transaction request is invalid")
    void shouldReturn400WhenCreateTransactionRequestIsInvalid() throws Exception {
        // Arrange
        TransactionRequestDTO invalidRequest = new TransactionRequestDTO(-10, "", null, null, null);

        // Act & Assert
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")));

        verify(transactionService, never()).createTransaction(any());
    }

    @Test
    @DisplayName("Should return all transactions")
    void shouldReturnAllTransactions() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        List<TransactionResponseDTO> transactions = List.of(
                new TransactionResponseDTO(1, 100, "Starbucks", now, 1),
                new TransactionResponseDTO(2, 200, "Amazon", now, 1));

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transaction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].merchant_or_business", is("Starbucks")))
                .andExpect(jsonPath("$[1].merchant_or_business", is("Amazon")));

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    @DisplayName("Should return transactions by client id")
    void shouldReturnTransactionsByClientId() throws Exception {
        // Arrange
        Integer clientId = 1;
        LocalDateTime now = LocalDateTime.now();
        List<TransactionResponseDTO> transactions = List.of(
                new TransactionResponseDTO(1, 100, "Starbucks", now, 1));

        when(transactionService.getTransactionsByClientId(clientId)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transaction/client/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].merchant_or_business", is("Starbucks")));

        verify(transactionService, times(1)).getTransactionsByClientId(clientId);
    }

    @Test
    @DisplayName("Should return transaction when id exists")
    void shouldReturnTransactionWhenIdExists() throws Exception {
        // Arrange
        Integer transactionId = 1;
        LocalDateTime now = LocalDateTime.now();
        TransactionResponseDTO response = new TransactionResponseDTO(transactionId, 100, "Starbucks", now, 1);

        when(transactionService.getTransactionById(transactionId)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/transaction/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(transactionId)))
                .andExpect(jsonPath("$.merchant_or_business", is("Starbucks")));

        verify(transactionService, times(1)).getTransactionById(transactionId);
    }

    @Test
    @DisplayName("Should return 400 when transaction is not found")
    void shouldReturn400WhenTransactionIsNotFound() throws Exception {
        // Arrange
        Integer transactionId = 99;
        when(transactionService.getTransactionById(transactionId))
                .thenThrow(new RuntimeException("Transaction not found"));

        // Act & Assert
        mockMvc.perform(get("/transaction/{id}", transactionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Transaction not found")))
                .andExpect(jsonPath("$.title", is("Business Logic Error")));

        verify(transactionService, times(1)).getTransactionById(transactionId);
    }

    @Test
    @DisplayName("Should update transaction when data is valid")
    void shouldUpdateTransactionWhenDataIsValid() throws Exception {
        // Arrange
        Integer transactionId = 1;
        LocalDateTime now = LocalDateTime.now();
        TransactionRequestDTO request = new TransactionRequestDTO(150, "Updated Shop", now, 1, 1);
        TransactionResponseDTO response = new TransactionResponseDTO(transactionId, 150, "Updated Shop", now, 1);

        when(transactionService.updateTransaction(eq(transactionId), any(TransactionRequestDTO.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/transaction/{id}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchant_or_business", is("Updated Shop")))
                .andExpect(jsonPath("$.amount", is(150)));

        verify(transactionService, times(1)).updateTransaction(eq(transactionId), any(TransactionRequestDTO.class));
    }

    @Test
    @DisplayName("Should return 429 when rate limit is exceeded")
    void shouldReturn429WhenRateLimitIsExceeded() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        TransactionRequestDTO request = new TransactionRequestDTO(100, "Starbucks", now, 1, 10); // Using client 10 to
                                                                                                 // avoid interference
        TransactionResponseDTO response = new TransactionResponseDTO(1, 100, "Starbucks", now, 1);

        when(transactionService.createTransaction(any(TransactionRequestDTO.class))).thenReturn(response);

        // Act: Perform 3 successful requests (limit is 3 per minute)
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/transaction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        // Assert: The 4th request should be blocked by RateLimitInterceptor
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message", is("Too many requests - Rate limit is 3 per minute for client 10")));

        // Service should only be called 3 times, as the 4th was blocked
        verify(transactionService, times(3)).createTransaction(any(TransactionRequestDTO.class));
    }

    @Test
    @DisplayName("Should delete transaction")
    void shouldDeleteTransaction() throws Exception {
        // Arrange
        Integer transactionId = 1;
        doNothing().when(transactionService).deleteTransaction(transactionId);

        // Act & Assert
        mockMvc.perform(delete("/transaction/{id}", transactionId))
                .andExpect(status().isNoContent());

        verify(transactionService, times(1)).deleteTransaction(transactionId);
    }
}
