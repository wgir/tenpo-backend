package com.tenpo.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransactionRequestDTO(
        @NotNull(message = "Amount is required") @Min(value = 0, message = "Amount cannot be negative") Integer amount,
        @NotBlank(message = "Merchant or business is required") String merchantOrBusiness,
        @NotNull(message = "Date is required") @PastOrPresent(message = "Date cannot be in the future") LocalDateTime date,
        @NotNull(message = "Employee ID is required") Integer employeeId,
        @NotNull(message = "Client ID is required") Integer clientId) {
}
