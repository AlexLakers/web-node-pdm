package com.alex.web.node.pdm.dto.specification;

import com.alex.web.node.pdm.model.Specification;
import jakarta.validation.constraints.NotBlank;

/**
 * This is input dto-object which contains all the necessary fields from {@link Specification specification}.
 * It is used for creation process a new specification.
 *
 * @param code code of specification.
 * @param desc description of specification.
 */

public record NewSpecificationDto(
        @NotBlank(message = "The code should be not blank ")
        String code,
        @NotBlank(message = "The description should be not blank ")
        String desc
) {
}
