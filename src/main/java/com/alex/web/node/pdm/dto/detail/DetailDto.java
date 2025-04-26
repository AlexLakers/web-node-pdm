package com.alex.web.node.pdm.dto.detail;


import com.alex.web.node.pdm.model.Detail;
import lombok.experimental.FieldNameConstants;
/**
 * This is output dto-object which contains all the necessary fields from {@link Detail detail}.
 * It's used for reading data about specific detail.
 *
 * @param id              id of detail.
 * @param name            name of detail.
 * @param amount          amount of detail.
 * @param specificationId id of specification.
 */

@FieldNameConstants
public record DetailDto(
        Long id,
        String name,
        Integer amount,
        Long specificationId
) {
}
