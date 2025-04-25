package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.model.Detail;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.search.DetailSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * This class is a service layer for {@link Detail detail}.
 */

public interface DetailService {
    /**
     * Returns dto-object by id of detail.
     *
     * @param id id of detail.
     * @return output-dto.
     */

    DetailDto findById(Long id);

    /**
     * Returns page of dto-object by input Pageable data.
     *
     * @param pageable input pagination dto.
     * @return page of output-dto.
     */

    Page<DetailDto> findAllByPageable(Pageable pageable);

    /**
     * Returns list of DetailDto by ids.
     *
     * @param searchDto list of details.
     * @return list od output-dto.
     */

    List<DetailDto> findAllByIds(DetailSearchDto searchDto);

    /**
     * Creates a new {@link Detail detail} by input-dto.
     *
     * @param newDetailDto input-dto.
     * @return output-dto.
     */

    DetailDto create(NewDetailDto newDetailDto);

    /**
     * Updates a specific {@link Detail detail} by input-dto and id.
     *
     * @param id              id of detail.
     * @param updateDetailDto input-dto.
     * @return output-dto.
     */

    DetailDto update(Long id, UpdateDetailDto updateDetailDto);

    /**
     * Removes a specific{@link Detail detail} by id of detail.
     *
     * @param id id of detail.
     * @return if deleting process successful the returns 'true',else-'false'.
     */

    boolean delete(Long id);

    /**
     * Returns list of output-dto by id of {@link Specification specifciation}.
     *
     * @param specId id of specification.
     * @return list of output-dto.
     */

    List<DetailDto> findAllBySpecId(Long specId);
}
