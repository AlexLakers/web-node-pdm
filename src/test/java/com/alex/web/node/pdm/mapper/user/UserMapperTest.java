package com.alex.web.node.pdm.mapper.user;

import com.alex.web.node.pdm.config.CustomUserDetails;
import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.dto.UserDto;
import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.Provider;
import com.alex.web.node.pdm.model.enums.RoleName;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    private User user;
    private UserDto userDto;
    private static final Long ID = 1L;
    private final static LocalDate DATE = LocalDate.of(1993,11,1);

    @Mock
    private UserMapperUtil userMapperUtil;

    @InjectMocks
    private UserMapperImpl userMapper;

    @BeforeEach
    void init() {
        user = User.builder().id(ID)
                .firstname("testFirstname").lastname("testLastname")
                .username("testCurrentName")
                .roles(Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build()))
                .birthday(DATE)
                .provider(Provider.DAO_LOCAL)
                .password("testPassword").build();

        userDto = new UserDto(ID, user.getUsername(), user.getFirstname()
                , user.getLastname(), DATE,
                List.of(RoleName.ADMIN.name()),Provider.DAO_LOCAL.name());
    }

    @Test
    void givenUser_whenMap_thenReturnCorrectUserDto() {
        when(userMapperUtil.roleToString(any(Role.class))).thenReturn(RoleName.ADMIN.name());
        when(userMapperUtil.providerToString(any(Provider.class))).thenReturn(Provider.DAO_LOCAL.name());

        UserDto actual = userMapper.toUserDto(user);

        Assertions.assertThat(actual).isEqualTo(userDto);
    }
    @Test
    void givenUser_whenMap_thenReturnCorrectSecurityUser(){
        CustomUserDetails customUserDetails =new CustomUserDetails(user.getUsername(),user.getPassword(),user.getRoles(),user.getId());

        CustomUserDetails actual=userMapper.toCustomUserDetails(user);

        Assertions.assertThat(actual).isEqualTo(customUserDetails);

    }

    @Test
    void givenUserList_whenMap_thenReturnCorrectUserDtoList() {
        List<User> givenUserList = Collections.singletonList(user);
        List<UserDto> expected = Collections.singletonList(userDto);
        when(userMapperUtil.roleToString(any(Role.class))).thenReturn(RoleName.ADMIN.name());
        when(userMapperUtil.providerToString(any(Provider.class))).thenReturn(Provider.DAO_LOCAL.name());

        List<UserDto> actual = userMapper.toUserDtoList(givenUserList);

        Assertions.assertThat(actual).hasSize(1).isEqualTo(expected);
    }

    @Test
    void givenNewUserDto_whenMap_thenReturnCorrectUser() {
        NewUserDto givenNewUserDto = new NewUserDto(user.getUsername(), user.getFirstname(), user.getUsername(),
                user.getPassword(), DATE);

        User actual = userMapper.toUserFromNewUserDto(givenNewUserDto);

        Assertions.assertThat(actual).isEqualTo(user);

    }

    @Test
    void givenUserAndUpdateUserDto_whenUpdate_thenUserUpdated() {
        final String POSTFIX_UPDATE = "update";
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getFirstname() + POSTFIX_UPDATE,
                user.getLastname() + POSTFIX_UPDATE, user.getUsername() + POSTFIX_UPDATE,
                DATE.plusYears(1));

        User expected = User.builder().id(ID)
                .firstname(user.getFirstname() + POSTFIX_UPDATE)
                .lastname(user.getLastname() + POSTFIX_UPDATE)
                .username(user.getUsername() + POSTFIX_UPDATE)
                .roles(Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build()))
                .birthday(user.getBirthday().plusYears(1))
                .password("testPassword").build();

        userMapper.updateUser(user, updateUserDto);

        Assertions.assertThat(user).isNotNull().isEqualTo(expected);

    }
}