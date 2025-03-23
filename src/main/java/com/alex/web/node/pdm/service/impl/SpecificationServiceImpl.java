package com.alex.web.node.pdm.service.impl;


import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.exception.CodeAlreadyExistsException;
import com.alex.web.node.pdm.exception.EntityCreationException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.mapper.specification.SpecificationMapper;
import com.alex.web.node.pdm.model.QSpecification;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.repository.SpecificationRepository;
import com.alex.web.node.pdm.search.PredicateBuilder;
import com.alex.web.node.pdm.search.SortBuilder;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.SpecificationService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
/*@PreAuthorize("#hasAnyAuthority('ADMIN','USER')")*/
public class SpecificationServiceImpl implements SpecificationService {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 1;
    private final SpecificationRepository specificationRepository;
    private final SpecificationMapper specificationMapper;


    public Page<SpecificationDto> findAll(SpecificationSearchDto searchDto) {
        Sort sort = SortBuilder.builder()
                .add(searchDto.orderDirection(), searchDto.orderColumn(), Sort.Order::new)
                .build();
        Predicate predicate = PredicateBuilder.builder()
                .add(searchDto.code(), QSpecification.specification.code::contains)
                .add(searchDto.userId(), QSpecification.specification.user.id::eq)
                .and();
        Pageable pageable = buildPageable(searchDto, sort);
        return specificationRepository.findAll(predicate, pageable)
                .map(specificationMapper::toSpecificationDto);
    }

  /*  public Page<SpecificationDto> findAll(SpecificationSearch search, Pageable pageable) {
        Predicate predicate = PredicateBuilder.builder()
                .add(search.code(), QSpecification.specification.code::contains)
                .add(search.userId(), QSpecification.specification.user.id::eq)
                .and();
        return specificationRepository.findAll(predicate, pageable).map(specificationMapper::toSpecificationDto);
    }*/

    private Pageable buildPageable(SpecificationSearchDto searchDto, Sort sort) {
        return PageRequest.of(
                searchDto.pageNumber() == null ? DEFAULT_PAGE_NUMBER : searchDto.pageNumber(),
                searchDto.pageSize() == null ? DEFAULT_PAGE_SIZE : searchDto.pageSize(),
                sort);

      /*  searchDto.pageNumber() == null && searchDto.pageSize() == null
                ? PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, sort)
                : PageRequest.of(searchDto.pageNumber(), searchDto.pageSize(), sort);*/
    }

    @Override
    public SpecificationDto findByCode(String code) {
        return specificationRepository.findByCode(code)
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> new EntityNotFoundException("The spec with code '%1$s' is not found".formatted(code)));
    }

    @Override
    public SpecificationDto findById(Long id) {
        return specificationRepository.findById(id)
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(id)));
    }

    @Override
    public List<SpecificationDto> findAllByUserId(Long userId) {
        return specificationMapper.toSpecificationDtoList(specificationRepository.findAllByUserId(userId));
    }


    @Override
    @Transactional
    public SpecificationDto create(NewSpecificationDto newSpecificationDto) {
        if (specificationRepository.existsByCode(newSpecificationDto.code()))
            throw new CodeAlreadyExistsException("The code %1$s is already exists".formatted(newSpecificationDto.code()));
        return Optional.of(newSpecificationDto)
                .map(specificationMapper::toSpecification)
                .map(specificationRepository::save)
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> new EntityCreationException("Spec '%1$s' creation error".formatted(newSpecificationDto.code())));
    }


    @Override
    @Transactional
    public SpecificationDto update(Long id, UpdateSpecificationDto dto) {
        Specification specification = specificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(id)));

        if (!specification.getCode().equals(dto.code()) && specificationRepository.existsByCode(dto.code()))
            throw new CodeAlreadyExistsException("The code %1$s is already exists".formatted(dto.code()));

        return Optional.of(specification)
                .map(spec -> {
                    specificationMapper.updateSpecification(dto, spec);
                    return specificationRepository.saveAndFlush(spec);
                })
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(id)));

    }


    @Override
    @Transactional

    public void delete(Long id) {
        specificationRepository.findById(id)
                .ifPresentOrElse(spec -> {
                            specificationRepository.delete(spec);
                            specificationRepository.flush();
                        },
                        () -> {
                            throw new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(id));
                        });
    }
}
