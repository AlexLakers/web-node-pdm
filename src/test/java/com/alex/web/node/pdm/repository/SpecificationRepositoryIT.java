package com.alex.web.node.pdm.repository;

import com.alex.web.node.pdm.model.Detail;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.model.User;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
class SpecificationRepositoryIT {
    private final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17");
    private static final Long INVALID_ID = 1000L;
    private static final Long VALID_ID = 1L;
    private static final String CODE_SPEC="testCode";
    private final User user = User.builder().id(1L).username("testUser").build();
    private final List<Detail> details = List.of(Detail.builder().name("testName").id(VALID_ID).amount(500).build());
    private final Specification specification = Specification.builder()
            .amount(1000).desc("desc").code("testCode").user(user).details(details).build();
    private final List<Specification> specifications = List.of(specification);

    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }
    @AfterAll
    static void beforeAll() {
        postgreSQLContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }

    private final SpecificationRepository specificationRepository;

    @ParameterizedTest
    @MethodSource("getArgs")
    void givenCode_whenExistsEntity_thenBooleanResult(String givenCode,boolean expected){
        boolean actual=specificationRepository.existsByCode(givenCode);
        assertThat(actual).isEqualTo(expected);
    }


    static Stream<Arguments> getArgs(){
        return Stream.of(
                Arguments.of("testCode",true),
                Arguments.of("testCode1",true),
                Arguments.of("testcode",false),
                Arguments.of("TESTCODE",false)
        );
    }


    @Test
    void givenUserId_whenFindSpecByUserId_thenReturnNotEmptyList(){
        List<Specification> actual=specificationRepository.findAllByUserId(VALID_ID);

        assertThat(actual).hasSize(2).contains(specification);
    }


}