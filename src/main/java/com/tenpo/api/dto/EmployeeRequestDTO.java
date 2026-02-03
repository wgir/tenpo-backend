package com.tenpo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EmployeeRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "RUT is required") String rut,
        @NotNull(message = "Client ID is required") Integer clientId) {
}
