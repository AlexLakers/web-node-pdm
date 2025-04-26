package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.exception.EntityCreationException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.exception.NameAlreadyExistsException;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.repository.SpecificationRepository;
import com.alex.web.node.pdm.repository.detail.DetailRepository;
import com.alex.web.node.pdm.search.DetailSearchDto;
import com.alex.web.node.pdm.service.impl.DetailServiceImpl;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class DetailServiceTest {
    private static final Long ID=1L;
    @Mock
    private DetailRepository detailRepository;
    @Mock
    private SpecificationRepository specificationRepository;
    @InjectMocks
    private DetailServiceImpl detailService;
    private final DetailDto detailDto=new DetailDto(ID,"testDetail",100,ID);

    @Test
    void givenId_whenFound_thenReturnDetailDto(){
        Mockito.when(detailRepository.findDetailById(ID)).thenReturn(Optional.of(detailDto));
        DetailDto actual=detailService.findById(ID);


        Assertions.assertThat(actual).isNotNull().isEqualTo(detailDto);
    }
    @Test
    void givenId_whenNotFound_thenThrowEntityNotFoundException(){
        Mockito.when(detailRepository.findDetailById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->detailService.findById(ID))
                .withMessageContaining("The detail with id '%1$d' is not found".formatted(ID));
    }
    @Test
    void givenNewDetailDto_whenSave_thenReturnDetailDtoWithId(){
        DetailDto detailDto=new DetailDto(ID + 1,"testDetail1",200, ID + 1);
        NewDetailDto givenNewDetailDto=new NewDetailDto("testDetail",100,ID);
        Mockito.when(detailRepository.saveDetail(givenNewDetailDto)).thenReturn(detailDto);
        Mockito.when(detailRepository.existsDetailByName("testDetail")).thenReturn(false);
        Mockito.when(specificationRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(Specification.builder().build()));
        Mockito.when(detailRepository.findDetailsByIds(Mockito.anyList())).thenReturn(List.of(detailDto));
        Mockito.doNothing().when(specificationRepository).flush();

        DetailDto actual=detailService.create(givenNewDetailDto);

        Mockito.verify(detailRepository, Mockito.times(1)).saveDetail(givenNewDetailDto);
        Mockito.verify(specificationRepository, Mockito.times(1)).flush();
        Mockito.verify(detailRepository, Mockito.times(1)).findDetailsByIds(Mockito.anyList());
        Mockito.verify(specificationRepository,Mockito.times(1)).findById(Mockito.anyLong());
        Assertions.assertThat(actual).isNotNull().isEqualTo(detailDto);
    }
    @Test
    void givenInvalidNewDetailDto_whenNotCreate_thenThrowNameAlreadyExistsException(){
        NewDetailDto givenNewDetailDto=new NewDetailDto("testDetail",100,ID);
        Mockito.when(detailRepository.existsDetailByName("testDetail")).thenReturn(true);

        Mockito.verifyNoMoreInteractions(detailRepository);
        Assertions.assertThatExceptionOfType(NameAlreadyExistsException.class)
                .isThrownBy(()->detailService.create(givenNewDetailDto))
                .withMessage("The detail with name '%1$s' already exists".formatted(givenNewDetailDto.name()));
    }
    @Test
    void givenInvalidNewDetailDto_whenNotCreate_thenThrowEntityCreationException(){
        NewDetailDto givenNewDetailDto=new NewDetailDto("testDetail",100,ID);
        Mockito.when(detailRepository.existsDetailByName("testDetail")).thenReturn(false);
        Mockito.when(specificationRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Mockito.verifyNoMoreInteractions(detailRepository);
        Assertions.assertThatExceptionOfType(EntityCreationException.class)
                .isThrownBy(()->detailService.create(givenNewDetailDto))
                .withMessage("The detail with name '%1$s' creation error".formatted(givenNewDetailDto.name()));
    }

    @Test
    void givenIdAndUpdateDetailDto_whenUpdate_thenReturnDetailDto(){
        UpdateDetailDto givenUpdateDetailDto=new UpdateDetailDto("updatedName",200);
        DetailDto expected=new DetailDto(ID,givenUpdateDetailDto.name(),givenUpdateDetailDto.amount(),ID);
        Mockito.when(detailRepository.findDetailById(ID)).thenReturn(Optional.of(detailDto));
        Mockito.when(detailRepository.updateDetail(ID,givenUpdateDetailDto)).thenReturn(expected);
        Mockito.when(specificationRepository.findById(ID)).thenReturn(Optional.of(Specification.builder().build()));
        DetailDto actual=detailService.update(ID,givenUpdateDetailDto);

        Assertions.assertThat(actual).isNotNull().isEqualTo(expected);
    }
    @Test
    void givenInvalidId_whenNotFound_theReturnDetailDto(){
        Mockito.when(detailRepository.findDetailById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->detailService.update(ID,null))
                .withMessage("The detail with id '%1$d' is not found".formatted(ID));
    }
    @Test
    void givenId_whenDelete_thenReturnTrue(){
        Mockito.when(detailRepository.findDetailById(ID)).thenReturn(Optional.of(detailDto));
        Mockito.when(detailRepository.deleteDetail(ID)).thenReturn(Boolean.TRUE);

        boolean actual = detailService.delete(ID);

        Assertions.assertThat(actual).isTrue();
    }
    @Test
    void givenNotValidId_whenNotFound_thenThrowEntityNotFoundException(){
        Mockito.when(detailRepository.findDetailById(ID)).thenReturn(Optional.empty());
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->detailService.delete(ID))
                .withMessage("The detail with id '%1$d' is not found".formatted(ID));
    }
    @Test
    void givenSpecId_whenFindAllDetails_thenReturnNotEmptyList(){
        List<DetailDto> expectedList= List.of(detailDto);
        Mockito.when(detailRepository.findDetailsBySpecificationId(ID)).thenReturn(expectedList);

        List<DetailDto> actual=detailService.findAllBySpecId(ID);

        Assertions.assertThat(actual).isNotNull().hasSize(1).contains(detailDto);
    }
    @Test
    void givenDetailSearchDto_whenFindAllDetails_thenReturnNotEmptyList(){
        List<DetailDto> expectedList= List.of(detailDto);
        List<Long> ids=List.of(ID,ID+ID);
        DetailSearchDto givenIds=new DetailSearchDto(ids);
        Mockito.when(detailRepository.findDetailsByIds(ids)).thenReturn(expectedList);

        List<DetailDto> actual=detailRepository.findDetailsByIds(ids);

        Assertions.assertThat(actual).isNotNull().hasSize(1).contains(detailDto);

    }

    @Test
    void givenPageable_whenFindAllDetails_thenReturnPageWithContent(){
        List<DetailDto> content= List.of(detailDto);
        Pageable givenPageable=PageRequest.of(0,1, Sort.by("id"));
        Page<DetailDto> expectedPage=new PageImpl<>(List.of(detailDto),givenPageable,1);
        Mockito.when(detailRepository.findDetailsBy(givenPageable)).thenReturn(expectedPage);

        Page<DetailDto> actual=detailRepository.findDetailsBy(givenPageable);

        Assertions.assertThat(actual.getSize()).isEqualTo(1);
        Assertions.assertThat(actual.getContent()).hasSize(1).isEqualTo(content);
        Assertions.assertThat(actual.getTotalElements()).isEqualTo(1);
    }

}