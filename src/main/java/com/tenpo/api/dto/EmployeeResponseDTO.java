package com.tenpo.api.dto;

import lombok.Builder;

@Builder
public record EmployeeResponseDTO(
        Integer id,
        String name,
        String rut,
        Integer clientId) {
}
