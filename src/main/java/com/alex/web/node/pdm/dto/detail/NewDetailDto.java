package com.alex.web.node.pdm.dto.detail;

import com.alex.web.node.pdm.model.Detail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * This is input dto-object which contains all the necessary fields from {@link Detail detail}.
 * It is used for creation process a new detail.
 *
 * @param name            name of detail.
 * @param amount          amount of detail.
 * @param specificationId id of specification.
 */

public record NewDetailDto(
        @NotBlank(message = "The name should be not empty")
        String name,
        Integer amount,
        @NotNull(message = "The specId should be not empty")
        Long specificationId
) {
}
