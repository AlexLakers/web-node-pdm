package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SpecificationService {
    SpecificationDto findByCode(String code);
    List<SpecificationDto> findAllByUserId(Long userId);
    SpecificationDto findById(Long id);
    Page<SpecificationDto> findAll(SpecificationSearchDto searchDto);
    /*Page<SpecificationDto> findAll(SpecificationSearch search, Pageable pageable);*/
    SpecificationDto create(NewSpecificationDto newSpecificationDto);
    SpecificationDto update(Long id, UpdateSpecificationDto updateSpecificationDto);
    void delete(Long id);
}
