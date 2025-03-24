package com.alex.web.node.pdm.dto.detail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDetailDto(
        @NotBlank(message = "The name should be not blank ")
        String name,
        @NotNull(message = "The amount should be not null ")
        Integer amount
) {
}
