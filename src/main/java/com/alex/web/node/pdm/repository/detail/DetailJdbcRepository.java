package com.alex.web.node.pdm.repository.detail;


import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface DetailJdbcRepository {
    List<DetailDto> findDetailsByIds(List<Long> ids);

    Optional<DetailDto> findDetailById(Long id);

    DetailDto saveDetail(NewDetailDto dto);

    boolean deleteDetail(Long id);

    DetailDto updateDetail(Long id, UpdateDetailDto dto);

    List<DetailDto> findDetailsBySpecificationId(Long id);

    Page<DetailDto> findDetailsBy(Pageable pageable);

    boolean existsDetailByName(String name);
}
