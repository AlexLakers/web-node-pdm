package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * This class is a service layer for {@link Specification specification}.
 */

public interface SpecificationService {

    /**
     * Returns dto-object by code of specification.
     *
     * @param code code of specification.
     * @return dto.
     */

    SpecificationDto findByCode(String code);

    /**
     * Returns list of dto-object by id of {@link User user}.
     *
     * @param userId id of user.
     * @return list of dto.
     */

    List<SpecificationDto> findAllByUserId(Long userId);

    /**
     * Returns dto-object by id of specification.
     *
     * @param id id of specification.
     * @return dto.
     */

    SpecificationDto findById(Long id);

    /**
     * Return the page with useful content by criteria.
     *
     * @param searchDto contains all the criteria of search.
     * @return page with data.
     */

    Page<SpecificationDto> findAll(SpecificationSearchDto searchDto);

    /**
     * Creates a new {@link Specification specification} by input-dto.
     *
     * @param newSpecificationDto input dto.
     * @return output-dto.
     */

    SpecificationDto create(NewSpecificationDto newSpecificationDto);

    /**
     * Updates  specific {@link Specification specification} by input-dto and id of spec.
     *
     * @param updateSpecificationDto input dto.
     * @param id  id of specification.
     * @return output-dto.
     */

    SpecificationDto update(Long id, UpdateSpecificationDto updateSpecificationDto);

    /**
     * Removes a specific {@link Specification specification} by id of spec.
     *
     * @param id id of spec.
     */

    void delete(Long id);
}
