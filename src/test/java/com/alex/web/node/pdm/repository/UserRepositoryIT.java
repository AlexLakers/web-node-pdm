package com.alex.web.node.pdm.repository;


import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.RoleName;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;
import java.util.stream.Stream;


/*@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)*/
/*@Sql({"classpath:sql/data.sql"})*/
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
@Transactional
class UserRepositoryIT {
    private final static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17");
    private static final Long INVALID_USER_ID = 1000L;
    private static final Long VALID_USER_ID = 1L;

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

    private final UserRepository userRepository;


    @Test
    void findById() {
        Role expectedRole = Role.builder().roleName(RoleName.USER).build();

        var actual = userRepository.findUserById(VALID_USER_ID);

        Assertions.assertThat(actual.isPresent()).isTrue();
        Assertions.assertThat(actual.get().getId()).isEqualTo(VALID_USER_ID);
        Assertions.assertThat(actual.get().getRoles())
                .isNotNull().hasSize(1).contains(expectedRole);
    }
    @ParameterizedTest
    @MethodSource("getArguments")
    void givenUsername_whenUserExists_thenReturnTrue(String username,boolean expected){


        boolean actual=userRepository.existsUserByUsername(username);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
    static Stream<Arguments> getArguments(){
        return Stream.of(
                Arguments.of("testadmin@mail.com",true),
                Arguments.of("TESTADMIN@mail.com",true),
                Arguments.of("TesTAdmiN@mail.com",true),
                Arguments.of("unknown@mail.com",false)

        );
    }

    @Test
    void givenUserId_whenFoundUser_thenReturnNotEmptyOptional(){

        Optional<User> actual=userRepository.findUserById(VALID_USER_ID);

        Assertions.assertThat(actual.isPresent()).isTrue();
        Assertions.assertThat(actual.get().getId()).isEqualTo(VALID_USER_ID);
    }

    @Test
    void givenInvalidId_whenNotFoundUser_thenReturnEmptyOptional(){
        Optional<User> actual=userRepository.findUserById(INVALID_USER_ID);

        Assertions.assertThat(actual.isPresent()).isFalse();
    }


}