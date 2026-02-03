package com.tenpo.api.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransactionResponseDTO(
        Integer id,
        Integer amount,
        String merchantOrBusiness,
        LocalDateTime date,
        Integer employeeId) {
}
