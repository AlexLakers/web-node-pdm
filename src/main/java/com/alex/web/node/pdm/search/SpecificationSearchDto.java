package com.alex.web.node.pdm.search;


import com.alex.web.node.pdm.validator.ValidOrderDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * This is dto-object for search-data transfer.
 * @param userId if of user.
 * @param code code of specification.
 * @param pageNumber page number for search.
 * @param pageSize size of page.
 * @param orderDirection direction of ordering(asc/desc).
 * @param orderColumn column-criteria for ordering process.
 */

public record SpecificationSearchDto(
                                     Long userId,
                                     String code,

                                     @PositiveOrZero(message = "Page number should be positive value")
                                     Integer pageNumber,

                                     @Positive(message = "Page size should be positive value")
                                     Integer pageSize,

                                     @ValidOrderDirection(message = "Order direction must be 'ASC' or 'DESC' values")
                                     String orderDirection,
                                     String orderColumn

){

}
