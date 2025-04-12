package com.alex.web.node.pdm;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.repository.SpecificationRepository;
import com.alex.web.node.pdm.search.DetailSearchDto;
import com.alex.web.node.pdm.service.DetailService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static com.alex.web.node.pdm.dto.detail.DetailDto.*;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
@Transactional
class DetailServiceImplDataBaseIT {
    private final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17");
    private static final Long ID = 1L;
    private static final Long INVALID_ID = Long.MAX_VALUE;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }

    private final DetailService detailService;
    private final SpecificationRepository specificationRepository;
    private final JdbcTemplate jdbcTemplate;
    private final DetailDto detailDto = new DetailDto(ID, "testDetail", 500, ID);


    @Test
    void givenDetailId_whenFindDetail_thenReturnDetailDto() {
        DetailDto actual = detailService.findById(ID);

        Assertions.assertThat(actual).isEqualTo(detailDto);
    }

    @Test
    void givenNotFoundDetailId_whenNotFindDetail_thenThrowEntityNotFoundException() {
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> detailService.findById(INVALID_ID))
                .withMessage("The detail with id '%1$s' is not found".formatted(INVALID_ID));
    }

    @Test
    void givenDetailSearchDto_whenFindAllDetailByIds_thenReturnPage() {
        DetailSearchDto givenSearchDto = new DetailSearchDto(List.of(ID, ID + 1));
        List<DetailDto> actual = detailService.findAllByIds(givenSearchDto);

        Assertions.assertThat(actual).hasSize(2).contains(detailDto);
    }

    @Test
    void givenPageable_whenFindAllDetailsByPageable_thenReturnPage() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id"));
        //List<DetailDto> content=List.of(detailDto);
        //Page<DetailDto> pageDetailDto=new PageImpl<>(content,pageable,2);

        Page<DetailDto> actual = detailService.findAllByPageable(pageable);

        Assertions.assertThat(actual.getContent()).hasSize(1).contains(detailDto);
        Assertions.assertThat(actual.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(actual.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(actual.getNumber()).isEqualTo(0);
    }

    @Test
    void givenSpecId_whenFindDetailsBySpecId_thenReturnListDetailDto() {
        List<DetailDto> actual = detailService.findAllBySpecId(ID);

        Assertions.assertThat(actual).hasSize(2).contains(detailDto);
    }

    @Test
    void givenNewDetailDto_whenCreate_thenReturnDetailDto() {
        jdbcTemplate.execute("SELECT SETVAL('detail_id_seq',(SELECT MAX(id) FROM detail))");
        NewDetailDto givenNewDetailDto = new NewDetailDto("NEW_NAME", 100, ID);

        DetailDto actual = detailService.create(givenNewDetailDto);

        Assertions.assertThat(actual)
                .hasFieldOrPropertyWithValue(Fields.id, 3L)
                .hasFieldOrPropertyWithValue(Fields.name, "NEW_NAME");
    }

    @Test
    void givenNewDetailDto_whenRecalculateAmountForParentSpecification_thenRecalculateAmount() {
        Specification spec = specificationRepository.findById(ID).get();
        Integer amountBeforeRecalculate = spec.getAmount();
        NewDetailDto givenNewDetailDto = new NewDetailDto("NEW_NAME", 100, ID);

        jdbcTemplate.execute("SELECT SETVAL('detail_id_seq',(SELECT MAX(id) FROM detail))");
        detailService.create(givenNewDetailDto);
        Integer amountAfterRecalculate = spec.getAmount();

        Assertions.assertThat(amountAfterRecalculate).isEqualTo(amountBeforeRecalculate + 200);
    }

    @Test
    void givenIdAndUpdateDetailDto_whenUpdate_thenReturnDetailDto() {
        UpdateDetailDto givenUpdateDetailDto = new UpdateDetailDto("UPDATE_NAME", 500);
        DetailDto actual = detailService.update(ID, givenUpdateDetailDto);

        Assertions.assertThat(actual)
                .hasFieldOrPropertyWithValue(Fields.id, ID)
                .hasFieldOrPropertyWithValue(Fields.name, "UPDATE_NAME");
    }
    @Test
    void givenNotValidIdAndUpdateDetailDto_whenUpdate_thenThrowEntityNotFoundException() {
        UpdateDetailDto givenUpdateDetailDto = new UpdateDetailDto("UPDATE_NAME", 500);

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> detailService.update(INVALID_ID, givenUpdateDetailDto))
                .withMessage("The detail with id '%1$s' is not found".formatted(INVALID_ID));
    }
    @Test
    void givenId_whenDelete_thenDetailNotFound(){
        detailService.delete(ID);

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->detailService.findById(ID))
                .withMessage("The detail with id '%1$s' is not found".formatted(ID));
    }

    @Test
    void givenInvalidId_whenNotFound_thenThrowEntityNotFoundException() {
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> detailService.findById(INVALID_ID))
                .withMessage("The detail with id '%1$s' is not found".formatted(INVALID_ID));
    }

}