package com.alex.web.node.pdm;


import com.alex.web.node.pdm.config.CustomUserDetails;
import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.dto.UserDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.exception.UsernameAlreadyExistsException;
import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.Provider;
import com.alex.web.node.pdm.model.enums.RoleName;
import com.alex.web.node.pdm.service.impl.UserServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ActiveProfiles("test")
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Transactional
@WithMockUser(username = "test@mail.com", authorities = {"ADMIN", "USER"}, password = "password")
public class UserServiceImplDataBaseIT {
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17");


    @BeforeAll
    static void startContainer() {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }

    private static final Long INVALID_USER_ID = 1000L;
    private static final Long VALID_USER_ID = 1L;
    private static final Long VALID_ADMIN_ID = 2L;

    private final UserServiceImpl userService;
    private final EntityManager entityManager;

    private final User user = User.builder().id(VALID_USER_ID).firstname("testUser").lastname("testUser")
            .birthday(LocalDate.of(1993, 1, 21))
            .provider(Provider.DAO_LOCAL)
            .password("testUser123")
            .roles(Collections.singletonList(Role.builder().roleName(RoleName.USER).build()))
            .username("testUser@mail.com")
            .build();
    private final User admin = User.builder().id(VALID_ADMIN_ID).firstname("testAdmin").lastname("testAdmin")
            .birthday(LocalDate.of(1993, 1, 21))
            .password("testAdmin123")
            .provider(Provider.OAUTH2_GOOGLE)
            .roles(Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build()))
            .username("testAdmin@mail.com")
            .build();
    private final List<String> roles = Collections.singletonList(Role.builder().roleName(RoleName.USER).build().getAuthority());


    @Test
    void givenUserId_whenFindUser_thenReturnUser() {
        List<String> roles = Collections.singletonList(Role.builder().roleName(RoleName.USER).build().getAuthority());
        UserDto expected = new UserDto(user.getId(), user.getUsername(), user.getFirstname(), user.getLastname(),
                user.getBirthday(), roles, Provider.DAO_LOCAL.name());

        UserDto actual = userService.findById(VALID_USER_ID);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenInvalidUserId_whenNotFindUser_thenThrowEntityNotFoundException() {

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> userService.findById(INVALID_USER_ID))
                .withMessage("The user '%1$s' is not found".formatted(INVALID_USER_ID));
    }

    @Test
    void findAllUser_thenReturnUserDtoList() {
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getFirstname(), user.getLastname(),
                user.getBirthday(), roles, user.getProvider().name());
        List<UserDto> userDtoList = userService.findAll();

        Assertions.assertThat(userDtoList).hasSize(2).contains(userDto);
    }

    @Test
    void givenIdAndUpdateUserDto_whenUpdate_thenReturnUpdatedUserDto() {
        final String POSTFIX_UPDATE = "update";
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getFirstname() + POSTFIX_UPDATE,
                user.getLastname() + POSTFIX_UPDATE, user.getUsername(), user.getBirthday().plusYears(1));
        UserDto expected = new UserDto(VALID_USER_ID, user.getUsername(),
                user.getFirstname() + POSTFIX_UPDATE, user.getLastname() + POSTFIX_UPDATE,
                user.getBirthday().plusYears(1), roles, Provider.DAO_LOCAL.name());

       UserDto actual= userService.update(VALID_USER_ID,updateUserDto);

       Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void givenInvalidIdAndUpdateUserDto_whenIdNotFound_thenThrowEntityNotFoundException() {
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getFirstname(),
                user.getLastname() , user.getUsername(), user.getBirthday());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->userService.update(INVALID_USER_ID,updateUserDto))
                .withMessage("The user '%1$s' is not found".formatted(INVALID_USER_ID));

    }


    @Test
    void givenUsername_whenLoadUser_thenReturnUserSecurity() {
        String givenUsername=user.getUsername();
        CustomUserDetails customUserDetails= new CustomUserDetails(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(RoleName.USER.name())), VALID_USER_ID);


        UserDetails actual = userService.loadUserByUsername(givenUsername);

        Assertions.assertThat(actual)
                .isExactlyInstanceOf(CustomUserDetails.class)
                .hasFieldOrPropertyWithValue("username", givenUsername)
                .hasFieldOrPropertyWithValue("id", VALID_USER_ID);
    }

    @Test
    void givenNewUserDto_whenCreate_thenReturnCorrectUserDtoWithId(){
        String setMaxPk= """
                SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));
                """;
        entityManager.createNativeQuery(setMaxPk).getSingleResult();
        String username="NEW_USER_"+user.getUsername();
        Long savedID=VALID_ADMIN_ID+1;
        NewUserDto givenNewUserDto = new NewUserDto(user.getFirstname(),user.getLastname(),
                username, user.getPassword(), user.getBirthday());

        UserDto actual= userService.create(givenNewUserDto);

        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("id",savedID);
    }

    @Test
    void givenNewUserDto_whenUsernameAlreadyExists_thenThrowUsernameAlreadyExistsException(){
        String existsUsername=user.getUsername();
        NewUserDto givenNewUserDto = new NewUserDto(user.getFirstname(),user.getLastname(),
                existsUsername, user.getPassword(), user.getBirthday());

        Assertions.assertThatExceptionOfType(UsernameAlreadyExistsException.class)
                .isThrownBy(()-> userService.create(givenNewUserDto))
                .withMessage("The username %1$s is already exists".formatted(existsUsername));
    }

    @Test
    void givenId_whenExistAndDelete_thenDelete(){
        userService.delete(VALID_USER_ID);
        Optional<User> actual=Optional.ofNullable(entityManager.find(User.class, VALID_USER_ID));

        Assertions.assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void givenId_whenNotFound_thenThrowNotFoundException(){
        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->userService.delete(INVALID_USER_ID))
                .withMessage("The user '%1$s' is not found".formatted(INVALID_USER_ID));
    }




}
