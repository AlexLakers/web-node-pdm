package com.alex.web.node.pdm.service.impl;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.exception.EntityCreationException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.exception.NameAlreadyExistsException;
import com.alex.web.node.pdm.model.Detail;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.repository.SpecificationRepository;
import com.alex.web.node.pdm.repository.detail.DetailRepository;
import com.alex.web.node.pdm.search.DetailSearchDto;
import com.alex.web.node.pdm.service.DetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DetailServiceImpl implements DetailService {
    private final DetailRepository detailRepository;
    private final SpecificationRepository specificationRepository;

    @Override
    public DetailDto findById(Long id) {
        log.debug("The method 'findById' with arg:{}", id);
        DetailDto dto=detailRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("The detail with id '%1$d' is not found".formatted(id)));
        log.info("Founded detail by id:{}", dto);
        return dto;
    }

    @Override
    public Page<DetailDto> findAllByPageable(Pageable pageable) {
        log.debug("The method 'findAllByPageable' with arg:{}", pageable);
        Page<DetailDto> dtoPage = detailRepository.findDetailsBy(pageable);
        log.info("Founded page of details by Pageable:{}", dtoPage);
        return dtoPage;
    }

    @Override
    public List<DetailDto> findAllByIds(DetailSearchDto searchDto) {
        log.debug("The method 'findAllByIds' with arg:{}", searchDto);
        List<DetailDto> dtoList = detailRepository.findDetailsByIds(searchDto.ids());
        log.info("Founded list of detail by ids:{}", dtoList);
        return dtoList;
    }

    @Override
    @Transactional
    public DetailDto create(NewDetailDto newDetailDto) {
        log.debug("The method 'create' with arg:{}", newDetailDto);
        if (detailRepository.existsDetailByName(newDetailDto.name())) {
            throw new NameAlreadyExistsException("The detail with name '%1$s' already exists".formatted(newDetailDto.name()));
        }
        DetailDto savedDto = specificationRepository.findById(newDetailDto.specificationId())
                .map(spec -> {
                    DetailDto savedDetailDto = detailRepository.saveDetail(newDetailDto);
                    Integer newAmountSpec = recalculateAmount(spec);
                    spec.setAmount(newAmountSpec);
                    specificationRepository.flush();
                    return savedDetailDto;
                }).orElseThrow(() -> new EntityCreationException("The detail with name '%1$s' creation error"
                        .formatted(newDetailDto.name())));
        log.info("A new detail with id:{} has been created",savedDto.id());
        return savedDto;


    }

    private Integer recalculateAmount(Specification spec) {
        List<Long> detailIds = spec.getDetails().stream()
                .map(Detail::getId)
                .collect(Collectors.toList());
        return detailRepository.findDetailsByIds(detailIds).stream()
                .map(DetailDto::amount)
                .reduce(0, Integer::sum);

    }

    @Override
    @Transactional
    public DetailDto update(Long id, UpdateDetailDto dto) {
        log.debug("The method 'update' with arg:{}", dto);
        DetailDto detailDto = detailRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("The detail with id '%1$d' is not found".formatted(id)));

        if (!(dto.name().equals(detailDto.name())) && detailRepository.existsDetailByName(dto.name()))
            throw new NameAlreadyExistsException("The detail with name '%1$s' already exists".formatted(dto.name()));

        DetailDto updatedDto=Optional.of(detailDto)
                .flatMap(detail -> specificationRepository.findById(detailDto.specificationId()))
                .map(spec -> {
                    DetailDto updatedDetailDto = detailRepository.updateDetail(id, dto);
                    Integer newAmountSpec = recalculateAmount(spec);
                    spec.setAmount(newAmountSpec);
                    specificationRepository.flush();
                    return updatedDetailDto;
                })
                .orElseThrow(() -> new EntityNotFoundException("The detail with id '%1$d' is not found".formatted(id)));
        log.info("The specification with id:{} has been updated",id);
        return updatedDto;
    }

    private DetailDto tryFindDetailById(Long id) {
        return detailRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("The detail with id '%1$d' is not found".formatted(id)));
    }

    private void checkIfTheNameIsAvailable(String name) {
        if (detailRepository.existsDetailByName(name))
            throw new NameAlreadyExistsException("The detail with name '%1$s' already exists".formatted(name));
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        log.debug("The method 'delete' with args:{}",id);
        return detailRepository.findDetailById(id)
                .map(detail -> detailRepository.deleteDetail(id))
                .orElseThrow(() -> new EntityNotFoundException("The detail with id '%1$d' is not found".formatted(id)));
    }

    @Override
    public List<DetailDto> findAllBySpecId(Long specId) {
        log.debug("The method 'findAllBySpecId' with args:{}",specId);
        return detailRepository.findDetailsBySpecificationId(specId);
    }

}