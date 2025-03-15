package com.alex.web.node.pdm.dto.specification;


import java.util.List;

public record SpecificationDto(
        Long id,
        String code,
        Integer amount,
        String desc,
        Long userId,
        List<Long> detailsIds
) {
}
