package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.search.DetailSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface DetailService {
    DetailDto findById(Long id);
    Page<DetailDto> findAllByPageable(Pageable pageable);
    List<DetailDto> findAllByIds(DetailSearchDto searchDto);
    DetailDto create(NewDetailDto newDetailDto);
    DetailDto update(Long id, UpdateDetailDto updateDetailDto);
    boolean delete(Long id);
    List<DetailDto> findAllBySpecId(Long specId);
}
