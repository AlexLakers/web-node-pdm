package com.alex.web.node.pdm.dto.specification;


import com.alex.web.node.pdm.model.Specification;

import java.util.List;

/**
 * This is output dto-object which contains all the necessary fields from {@link Specification specification}.
 * It's used for reading data about specific specification.
 *
 * @param id         id of specification.
 * @param code       code of specification.
 * @param amount     amount of specification.
 * @param desc       description of specification.
 * @param userId     id of user-owner.
 * @param detailsIds list of id(details).
 */

public record SpecificationDto(
        Long id,
        String code,
        Integer amount,
        String desc,
        Long userId,
        List<Long> detailsIds
) {
}
