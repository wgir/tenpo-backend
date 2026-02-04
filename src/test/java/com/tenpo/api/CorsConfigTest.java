package com.tenpo.api;

import com.tenpo.config.WebConfig;
import com.tenpo.interceptor.RateLimitInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import({ WebConfig.class, RateLimitInterceptor.class })
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private com.tenpo.service.ClientService clientService;

    @MockitoBean
    private com.tenpo.service.TransactionService transactionService;

    @Test
    @DisplayName("Should return CORS headers for preflight request")
    void shouldReturnCorsHeadersForPreflightRequest() throws Exception {
        mockMvc.perform(options("/client/2")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET"));
    }
}
