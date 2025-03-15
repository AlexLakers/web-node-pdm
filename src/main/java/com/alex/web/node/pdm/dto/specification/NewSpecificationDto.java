package com.alex.web.node.pdm.dto.specification;

import jakarta.validation.constraints.NotBlank;

public record NewSpecificationDto(
        @NotBlank(message = "The code should be not blank ")
        String code,
        @NotBlank(message = "The description should be not blank ")
        String desc
) {
}
