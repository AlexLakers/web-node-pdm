package com.alex.web.node.pdm.dto.detail;

import com.alex.web.node.pdm.model.Detail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * This is input dto-object which contains all the necessary fields from {@link Detail detail}.
 * It is used for updating a specific detail.
 *
 * @param name   name of detail.
 * @param amount amount of detail.
 */

public record UpdateDetailDto(
        @NotBlank(message = "The name should be not blank ")
        String name,
        @NotNull(message = "The amount should be not null ")
        Integer amount
) {
}
