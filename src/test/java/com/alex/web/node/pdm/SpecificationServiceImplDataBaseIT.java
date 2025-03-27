package com.alex.web.node.pdm;


import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.exception.CodeAlreadyExistsException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.SpecificationService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
@RequiredArgsConstructor
@Transactional
@WithMockUser(username = "testAdmin@mail.com", authorities = {"ADMIN", "USER"}, password = "password")
class SpecificationServiceImplDataBaseIT {

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17");
    private final static Long ID = 1L;
    private final static Long INVALID_ID = 3L;

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }

    private final SpecificationService specificationService;
    private final NewSpecificationDto newSpecificationDto = new NewSpecificationDto("testCode", "testDesc");
    private final SpecificationDto specificationDto = new SpecificationDto(ID, "testCode", 1000, "desc", ID, List.of(1L, 2L));
    private final EntityManager entityManager;

    //final Specification specification = Specification.builder().id(ID).desc("testDesc").code("testCode").build();

    @Test
    void givenSpecId_whenFindSpec_thenReturnSpecDto() {
        SpecificationDto actual = specificationService.findById(ID);

        Assertions.assertThat(actual).isEqualTo(specificationDto);
    }

    @Test
    void givenNotFoundSpecId_whenNotFindSpec_thenThrowEntityNotFoundException() {
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> specificationService.findById(INVALID_ID))
                .withMessage("The spec with id '%1$s' is not found".formatted(INVALID_ID));
    }

    @Test
    void givenSpecSearchDto_whenFindAllSpecByFilter_thenReturnPage() {
        SpecificationSearchDto givenSearchDto = new SpecificationSearchDto(ID, "testCode", 0, 2, "ASC", "code");

        Page<SpecificationDto> actual = specificationService.findAll(givenSearchDto);

        Assertions.assertThat(actual).hasSize(2).contains(specificationDto);
    }

    @Test
    void givenIdAndUpdateSpecDto_whenUpdate_thenReturnUpdatedSpecDto() {

        final String POSTFIX_UPDATE = "update";
        UpdateSpecificationDto givenUpdateSpecDto = new UpdateSpecificationDto(specificationDto.code() + POSTFIX_UPDATE, specificationDto.desc() + POSTFIX_UPDATE);
        SpecificationDto expected = new SpecificationDto(ID, givenUpdateSpecDto.code(), specificationDto.amount(), givenUpdateSpecDto.desc(), ID, specificationDto.detailsIds());

        SpecificationDto actual=specificationService.update(ID, givenUpdateSpecDto);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
    @Test
    void givenInvalidIdAndUpdateSpecDto_whenIdNotFound_thenThrowEntityNotFoundException() {
        UpdateSpecificationDto givenUpdateSpecDto = new UpdateSpecificationDto(specificationDto.code(), specificationDto.desc());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->specificationService.update(INVALID_ID, givenUpdateSpecDto))
                .withMessage("The spec with id '%1$s' is not found".formatted(INVALID_ID));
    }
    @Test
    void givenNewSpecDto_whenCreate_thenReturnCorrectSpecDtoWithId(){
        String setMaxPk= """
                SELECT SETVAL('specification_id_seq', (SELECT MAX(id) FROM specification));
                """;
        entityManager.createNativeQuery(setMaxPk).getSingleResult();
        NewSpecificationDto givenNewSpecificationDto=new NewSpecificationDto("NEW_TEST_CODE","desc");
        Long savedId=ID+2;
        SpecificationDto actual=specificationService.create(givenNewSpecificationDto);

        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("code", givenNewSpecificationDto.code())
                .hasFieldOrPropertyWithValue("desc", givenNewSpecificationDto.desc())
                .hasFieldOrPropertyWithValue("id",savedId);

    }

    @Test
    void givenNewSpecDto_whenCodeAlreadyExists_thenThrowCodeAlreadyExistsException(){
        NewSpecificationDto givenNewSpecificationDto=new NewSpecificationDto("testCode","desc");

        Assertions.assertThatExceptionOfType(CodeAlreadyExistsException.class)
                .isThrownBy(()-> specificationService.create(givenNewSpecificationDto))
                .withMessage("The code %1$s is already exists".formatted("testCode"));
    }

    @Test
    void givenId_whenExistAndDelete_thenDelete(){
        specificationService.delete(ID);
        Optional<Specification> actual=Optional.ofNullable(entityManager.find(Specification.class, ID));

        Assertions.assertThat(actual).isEmpty();
    }
    @Test
    void givenId_whenNotFound_thenThrowNotFoundException(){
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->specificationService.delete(INVALID_ID))
                .withMessage("The spec with id '%1$s' is not found".formatted(INVALID_ID));
    }
}

