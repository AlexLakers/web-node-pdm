package com.alex.web.node.pdm.repository.detail;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.alex.web.node.pdm.dto.detail.DetailDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
class DetailRepositoryIT {
    private static final Long ID = 1L;
    private static final PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:17");

    @BeforeAll
    static void startContainer() {
        postgreSqlContainer.start();
    }
    @AfterAll
    static void beforeAll() {
        postgreSqlContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
    }

    private final DetailRepository detailRepository;
    private final JdbcTemplate jdbcTemplate;
    private final DetailDto detailDto = new DetailDto(ID, "testDetail", 500, ID);

    @Test
    void givenId_whenFound_thenReturnDetailDto() {
        Optional<DetailDto> actual = detailRepository.findDetailById(ID);

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(detailDto);
    }

    @Test
    void givenNotFoundId_whenNotFound_thenReturnEmptyOptional() {
        Optional<DetailDto> actual = detailRepository.findDetailById(Long.MAX_VALUE);

        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void givenNewDetailDto_whenSave_thenReturnSavedDetailDto() {
        Long expectedId = 3L;
        NewDetailDto givenNewDetailDto = new NewDetailDto("newDetail", 600, ID);
        jdbcTemplate.execute("SELECT SETVAL('detail_id_seq',(SELECT MAX(id) FROM detail))");

        DetailDto actual = detailRepository.saveDetail(givenNewDetailDto);

        assertThat(actual)
                .hasFieldOrPropertyWithValue(Fields.id, expectedId)
                .hasFieldOrPropertyWithValue(Fields.name, "newDetail");
    }

    @Test
    void givenId_whenDelete_thenFindById() {
        detailRepository.deleteDetail(ID);

        assertThat(detailRepository.findDetailById(ID).isEmpty()).isTrue();
    }

    @Test
    void givenIdAndUpdateDetailDto_whenUpdate_thenDeleteDetail() {
        UpdateDetailDto givenUpdateDetailDto = new UpdateDetailDto("updateDetail", 1000);

        DetailDto actual = detailRepository.updateDetail(ID, givenUpdateDetailDto);

        assertThat(actual)
                .hasFieldOrPropertyWithValue(Fields.name, "updateDetail")
                .hasFieldOrPropertyWithValue(Fields.amount, 1000)
                .hasFieldOrPropertyWithValue(Fields.id, ID);
    }

    @Test
    void givenIds_whenFindSomeThing_thenReturnNotEmptyList() {
        List<Long> givenIds = List.of(1L, 2L);

        List<DetailDto> actual = detailRepository.findDetailsByIds(givenIds);

        assertThat(actual)
                .hasSize(2)
                .contains(detailDto);
    }


    @Test
    void givenPageable_whenFound_thenReturnPage() {
        Sort sort = Sort.by(Fields.amount);
        Pageable pageable = PageRequest.of(0, 1, sort);

        Page<DetailDto> actual = detailRepository.findDetailsBy(pageable);

        assertThat(actual.getContent()).hasSize(1).contains(detailDto);
        assertThat(actual.getTotalElements()).isEqualTo(2);
        assertThat(actual.getSize()).isEqualTo(1);
    }
    @ParameterizedTest
    @MethodSource("getArgs")
    void givenName_whenDetailExists_returnTrue(boolean expected,String givenName){
        boolean actual=detailRepository.existsDetailByName(givenName);

        assertThat(actual).isEqualTo(expected);
    }
    static Stream<Arguments> getArgs(){
        return Stream.of(
                Arguments.of(true,"testDetail"),
                Arguments.of(false,"testNameNotFound"),
                Arguments.of(false,"")
        );
    }




}