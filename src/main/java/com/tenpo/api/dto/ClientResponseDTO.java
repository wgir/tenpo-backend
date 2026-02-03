package com.tenpo.api.dto;

import lombok.Builder;

@Builder
public record ClientResponseDTO(
        Integer id,
        String name,
        String rut) {
}
