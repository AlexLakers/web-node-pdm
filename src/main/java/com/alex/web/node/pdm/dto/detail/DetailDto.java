package com.alex.web.node.pdm.dto.detail;


import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public record DetailDto(
        Long id,
        String name,
        Integer amount,
        Long specificationId
) {
}
