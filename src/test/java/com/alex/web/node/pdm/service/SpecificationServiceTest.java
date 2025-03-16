package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.exception.CodeAlreadyExistsException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.mapper.specification.SpecificationMapper;
import com.alex.web.node.pdm.model.Detail;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.repository.SpecificationRepository;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.impl.SpecificationServiceImpl;
import com.querydsl.core.types.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SpecificationServiceTest {
    private static final Long ID = 1L;

    private final User user = User.builder().id(ID).username("testadmin@mail.com").build();
    private final List<Detail> details = List.of(Detail.builder().name("testName").id(ID).amount(500).build());
    private final List<Long> detailsIds = List.of(ID);
    private final Specification specification = Specification.builder()
            .amount(1000).desc("desc").code("testCode").user(user).details(details).build();
    private final SpecificationDto specificationDto = new SpecificationDto(ID, "testCode", 100, "desc", ID, detailsIds);
    private final List<SpecificationDto> specificationDtoList = Collections.singletonList(specificationDto);
    @Mock
    private SpecificationRepository specificationRepository;
    @Mock
    private SpecificationMapper specificationMapper;
    @InjectMocks
    private SpecificationServiceImpl specificationService;


    @Test
    void findAll_thenReturnListSpecificationDto() {
        SpecificationSearchDto searchDto = new SpecificationSearchDto(ID, "testCode", 0, 2, "ASC", "code");
        Pageable pageable = PageRequest.of(searchDto.pageNumber(), searchDto.pageSize(), Sort.by("code").ascending());
        List<Specification> specifications = Collections.singletonList(specification);
        Page<Specification> specificationPage = new PageImpl<>(specifications, pageable, 1);
        Mockito.when(specificationRepository.findAll(Mockito.any(Predicate.class), Mockito.any(Pageable.class))).thenReturn(specificationPage);
        Mockito.when(specificationMapper.toSpecificationDto(specification)).thenReturn(specificationDto);

        Page<SpecificationDto> actual = specificationService.findAll(searchDto);

        Assertions.assertThat(actual).hasSize(1).contains(specificationDto);
        Mockito.verifyNoMoreInteractions(specificationRepository, specificationMapper);

    }

    @Test
    void givenId_whenFoundEntity_thenReturnCorrectSpecificationDto() {
        Mockito.when(specificationRepository.findById(ID)).thenReturn(Optional.ofNullable(specification));
        Mockito.when(specificationMapper.toSpecificationDto(specification)).thenReturn(specificationDto);

        SpecificationDto actual = specificationService.findById(ID);

        Assertions.assertThat(actual).isEqualTo(specificationDto);
    }

    @Test
    void givenId_whenNotFoundEntity_thenThrowEntityNotFoundException() {
        Mockito.when(specificationRepository.findById(ID)).thenReturn(Optional.empty());

        Mockito.verifyNoInteractions(specificationMapper);
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> specificationService.findById(ID))
                .withMessage("The spec with id '%1$s' is not found".formatted(ID));

    }

    @Test
    void givenCode_whenFoundEntity_thenReturnCorrectSpecificationDto() {
        final String code = "testCode";
        Mockito.when(specificationRepository.findByCode(code)).thenReturn(Optional.ofNullable(specification));
        Mockito.when(specificationMapper.toSpecificationDto(specification)).thenReturn(specificationDto);

        SpecificationDto actual = specificationService.findByCode(code);

        Assertions.assertThat(actual).isEqualTo(specificationDto);
    }

    @Test
    void givenCode_whenNotFoundEntity_thenThrowEntityNotFoundException() {
        final String code = "testCode";
        Mockito.when(specificationRepository.findByCode(code)).thenReturn(Optional.empty());

        Mockito.verifyNoInteractions(specificationMapper);
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> specificationService.findByCode(code))
                .withMessageContaining("The spec with code '%1$s' is not found".formatted(code));
    }

    @Test
    void givenIdAndUpdateSpecificationDto_whenUpdate_thenReturnNotEmptySpecificationDto() {
        final String POSTFIX_UPDATE = "update";
        UpdateSpecificationDto updateSpecificationDto = new UpdateSpecificationDto(specification.getCode() + POSTFIX_UPDATE, specification.getDesc() + POSTFIX_UPDATE);
        SpecificationDto expected=new SpecificationDto(ID,updateSpecificationDto.code(),specification.getAmount(), updateSpecificationDto.desc(), ID,null);
        Mockito.when(specificationRepository.findById(ID)).thenReturn(Optional.of(specification));
        Mockito.doNothing().when(specificationMapper).updateSpecification(updateSpecificationDto,specification);
        Mockito.when(specificationMapper.toSpecificationDto(specification)).thenReturn(expected);
        Mockito.when(specificationRepository.saveAndFlush(specification)).thenReturn(specification);

        SpecificationDto actual=specificationService.update(ID,updateSpecificationDto);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenIdAndUpdateSpecificationDto_whenNotFoundEntity_thenThrowEntityNotFoundException() {
        UpdateSpecificationDto updateSpecificationDto = new UpdateSpecificationDto(specification.getCode() , specification.getDesc());
        Mockito.when(specificationRepository.findById(ID)).thenReturn(Optional.empty());

        Mockito.verifyNoInteractions(specificationMapper);
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> specificationService.update(ID,updateSpecificationDto))
                .withMessage("The spec with id '%1$s' is not found".formatted(ID));
    }
    @Test
    void givenNewSpecificationDto_whenCreate_thenReturnSpecificationDtoWithId() {
        NewSpecificationDto newSpecificationDto = new NewSpecificationDto(specification.getCode(),specification.getDesc());
        Mockito.when(specificationRepository.existsByCode(specification.getCode())).thenReturn(false);
        Mockito.when(specificationRepository.save(specification)).thenReturn(specification);
        Mockito.when(specificationMapper.toSpecification(newSpecificationDto)).thenReturn(specification);
        Mockito.when(specificationMapper.toSpecificationDto(specification)).thenReturn(specificationDto);

        SpecificationDto actual = specificationService.create(newSpecificationDto);

        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("id", ID);
    }
    @Test
    void givenNewSpecificationDto_whenCodeAlreadyExists_thenThrowCodeAlreadyExistsException(){
        NewSpecificationDto givenNewSpecificationDto = new NewSpecificationDto(specification.getCode(),specification.getDesc());
        Mockito.when(specificationRepository.existsByCode(Mockito.anyString())).thenReturn(true);

        Assertions.assertThatExceptionOfType(CodeAlreadyExistsException.class)
                .isThrownBy(()->specificationService.create(givenNewSpecificationDto))
                .withMessage("The code %1$s is already exists".formatted(specification.getCode()));
    }
    @Test
    void givenId_whenExistAndDelete_thenDelete(){
        Mockito.when(specificationRepository.findById(ID)).thenReturn(Optional.ofNullable(specification));
        Mockito.doNothing().when(specificationRepository).delete(Mockito.any(Specification.class));
        Mockito.doNothing().when(specificationRepository).flush();

        specificationService.delete(ID);

        Mockito.verify(specificationRepository,Mockito.times(1)).delete(specification);
        Mockito.verify(specificationRepository,Mockito.times(1)).flush();
        Mockito.verify(specificationRepository,Mockito.times(1)).findById(ID);
        Mockito.verifyNoMoreInteractions(specificationRepository);
    }
    @Test
    void givenId_whenNotFound_thenThrowEntityNotFoundException() {
        Mockito.when(specificationRepository.findById(ID)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> specificationService.delete(ID))
                .withMessage("The spec with id '%1$s' is not found".formatted(ID));
    }



}