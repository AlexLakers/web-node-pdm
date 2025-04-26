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
import com.alex.web.node.pdm.service.LogMessageService;
import com.alex.web.node.pdm.service.SpecificationService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
/*@PreAuthorize("#hasAnyAuthority('ADMIN','USER')")*/
public class SpecificationServiceImpl implements SpecificationService {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 1;
    private final SpecificationRepository specificationRepository;
    private final SpecificationMapper specificationMapper;
    private final LogMessageService logMessageService;


    public Page<SpecificationDto> findAll(SpecificationSearchDto searchDto) {
        log.debug("The method 'findAll' with arg:{}", searchDto);
        Sort sort = SortBuilder.builder()
                .add(searchDto.orderDirection(), searchDto.orderColumn(), Sort.Order::new)
                .build();
        Predicate predicate = PredicateBuilder.builder()
                .add(searchDto.code(), QSpecification.specification.code::contains)
                .add(searchDto.userId(), QSpecification.specification.user.id::eq)
                .and();
        Pageable pageable = buildPageable(searchDto, sort);
        Page<SpecificationDto> page = specificationRepository.findAll(predicate, pageable)
                .map(specificationMapper::toSpecificationDto);
        log.info("Page of specifications has been generated:{}", page);
        return page;
    }

    private Pageable buildPageable(SpecificationSearchDto searchDto, Sort sort) {
        return PageRequest.of(
                searchDto.pageNumber() == null ? DEFAULT_PAGE_NUMBER : searchDto.pageNumber(),
                searchDto.pageSize() == null ? DEFAULT_PAGE_SIZE : searchDto.pageSize(),
                sort);

    }


    @Override
    public SpecificationDto findByCode(String code) {
        log.debug("The method 'findByCode' with arg:{}", code);
        SpecificationDto dto = specificationRepository.findByCode(code)
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> new EntityNotFoundException("The spec with code '%1$s' is not found".formatted(code)));
        log.info("Founded specification by code:{}", dto);
        return dto;
    }


    @Override
    public SpecificationDto findById(Long id) {
        log.debug("The method 'findById' with arg:{}", id);
        SpecificationDto dto = specificationRepository.findById(id)
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> logAndThrowEntityNotFound(id)); /*new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(id))*/
        log.info("Founded specification by id:{}", dto);
        return dto;
    }


    @Override
    public List<SpecificationDto> findAllByUserId(Long userId) {
        log.debug("The method 'findAllByUserId' with arg:{}", userId);
        List<SpecificationDto> dtoList = specificationMapper.toSpecificationDtoList(specificationRepository.findAllByUserId(userId));
        log.info("Founded list of specification by userId:{}", dtoList);
        return dtoList;
    }


    @Override
    @Transactional
    public SpecificationDto create(NewSpecificationDto newSpecificationDto) {
        log.debug("The method 'create' with arg:{}", newSpecificationDto);
        logAndThrowIfCodeIsExists(newSpecificationDto.code());
        SpecificationDto dto= Optional.of(newSpecificationDto)
                .map(specificationMapper::toSpecification)
                .map(specificationRepository::save)
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> new EntityCreationException("Spec '%1$s' creation error".formatted(newSpecificationDto.code())));
        log.info("Created a new specification:{}",dto);
        return dto;
    }


    private void logAndThrowIfCodeIsExists(String code) {
        if (specificationRepository.existsByCode(code)) {
            String errorMessage = "The code %1$s is already exists".formatted(code);
            logMessageService.save(errorMessage);
            throw new CodeAlreadyExistsException(errorMessage);
        }
    }

    private EntityNotFoundException logAndThrowEntityNotFound(Long id) {
        String errorMessage = "The spec with id '%1$s' is not found".formatted(id);
        logMessageService.save(errorMessage);
        return new EntityNotFoundException(errorMessage);
    }


    @Override
    @Transactional
    public SpecificationDto update(Long id, UpdateSpecificationDto dto) {
        log.debug("The method 'update' with arg:{}", dto);
        Specification specification = specificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(id)));

        if (!specification.getCode().equals(dto.code())) {
            logAndThrowIfCodeIsExists(dto.code());
        }
        SpecificationDto updatedDto=Optional.of(specification)
                .map(spec -> {
                    specificationMapper.updateSpecification(dto, spec);
                    return specificationRepository.saveAndFlush(spec);
                })
                .map(specificationMapper::toSpecificationDto)
                .orElseThrow(() -> logAndThrowEntityNotFound(id));
        log.info("Updated specification:{}",updatedDto);
        return updatedDto;

    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("The method 'delete' with arg:{}", id);
        specificationRepository.findById(id)
                .ifPresentOrElse(spec -> {
                            specificationRepository.delete(spec);
                            specificationRepository.flush();
                        },
                        () -> {
                            throw logAndThrowEntityNotFound(id);
                        });
        log.info("The specification with id:{} has been deleted", id);
    }
}
