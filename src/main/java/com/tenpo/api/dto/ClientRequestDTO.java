package com.tenpo.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ClientRequestDTO(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "RUT is required") String rut) {
}
