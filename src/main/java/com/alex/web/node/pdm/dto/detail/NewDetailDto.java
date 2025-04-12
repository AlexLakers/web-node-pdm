package com.alex.web.node.pdm.dto.detail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record NewDetailDto(
        @NotBlank(message = "The name should be not empty")
        String name,
        Integer amount,
        @NotNull(message = "The specId should be not empty")
        Long specificationId
) {
}
