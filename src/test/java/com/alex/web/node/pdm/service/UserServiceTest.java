package com.alex.web.node.pdm.service;



import com.alex.web.node.pdm.config.CustomUserDetails;
import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.dto.UserDto;
import com.alex.web.node.pdm.exception.EntityCreationException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.exception.UsernameAlreadyExistsException;
import com.alex.web.node.pdm.mapper.user.UserMapper;
import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.Provider;
import com.alex.web.node.pdm.model.enums.RoleName;
import com.alex.web.node.pdm.repository.UserRepository;
import com.alex.web.node.pdm.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final Long ID = 1L;
    private static final LocalDate DATE = LocalDate.of(1993, 1, 1);
    private User user;
    private UserDto userDto;

    @BeforeEach
    void initData() {
        user = User.builder().id(ID).firstname("testUser").lastname("testUser")
                .birthday(LocalDate.of(1993, 1, 1))
                .password("testPassword")
                .roles(Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build())).username("testUser")
                .build();
        List<String> roles = Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build().getAuthority());
        userDto = new UserDto(ID, user.getUsername(), user.getFirstname(), user.getLastname(), DATE, roles, Provider.DAO_LOCAL.name());
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void givenUsername_whenLoadUser_thenReturnUserSecurity() {
        CustomUserDetails customUserDetails= new CustomUserDetails("testUser", "testPassword",
                Collections.singletonList(new SimpleGrantedAuthority("ADMIN")), ID);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userMapper.toCustomUserDetails(user)).thenReturn(customUserDetails);

        UserDetails actual = userService.loadUserByUsername("testUser");

        assertThat(actual)
                .isExactlyInstanceOf(CustomUserDetails.class)
                .isEqualTo(customUserDetails);
    }

    @Test
    void givenId_whenFindUser_thenReturnCorrectUserDto() {
        when(userRepository.findUserById(ID)).thenReturn(Optional.ofNullable(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto actual = userService.findById(ID);

        assertThat(actual).isEqualTo(userDto);
    }

    @Test
    void findAll_thenReturnListUserDto() {
        List<User> users = Arrays.asList(user, User.builder().id(ID + 1).build());
        List<String> roles = Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build().getAuthority());
        List<UserDto> expected = Arrays.asList(new UserDto(ID, user.getUsername(), user.getFirstname(), user.getLastname(), DATE, roles,Provider.DAO_LOCAL.name()),
                new UserDto(ID + 1L, user.getUsername(), user.getFirstname(), user.getLastname(), DATE, roles,Provider.DAO_LOCAL.name()));
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserDtoList(users)).thenReturn(expected);

        List<UserDto> actual = userService.findAll();

        assertThat(actual).hasSize(2).isEqualTo(expected);

    }

    @Test
    void givenIdAndUpdateUserDto_whenUpdate_thenReturnNotEmptyUserDto() {
        final String POSTFIX_UPDATE = "update";
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getFirstname() + POSTFIX_UPDATE,
                user.getLastname() + POSTFIX_UPDATE, user.getUsername() + POSTFIX_UPDATE, DATE);
        UserDto expected = (new UserDto(ID, user.getUsername() + POSTFIX_UPDATE,
                user.getFirstname() + POSTFIX_UPDATE, user.getLastname() + POSTFIX_UPDATE, DATE, null,null));
        when(userRepository.findUserById(ID)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUser(user, updateUserDto);
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expected);

        UserDto actual = userService.update(ID, updateUserDto);

        assertThat(actual).isEqualTo(expected);

    }
    @Test
    void givenIdAdnUpdateUserDto_whenIdNotFound_thenThrowEntityNotFoundException() {
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getFirstname(), user.getLastname(), user.getUsername(), DATE);
        when(userRepository.findUserById(ID)).thenReturn(Optional.empty());

        verify(userMapper, never()).updateUser(any(User.class), any(UpdateUserDto.class));
        verify(userRepository, never()).saveAndFlush(user);
        verifyNoMoreInteractions(userRepository,userMapper);
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()-> userService.update(ID,updateUserDto))
                .withMessage("The user '%1$s' is not found".formatted(ID));
    }
    @Test
    void givenNewUserDto_whenCreate_thenReturnNotEmptyUserDto(){
        NewUserDto givenNewUserDto = new NewUserDto(user.getUsername(), user.getFirstname(),
                user.getUsername(), user.getPassword(), DATE);
        User userWithoutId = User.builder().firstname(user.getFirstname()).password(user.getPassword())
                .username(user.getUsername()).build();
        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(false);
        when(userMapper.toUserFromNewUserDto(givenNewUserDto)).thenReturn(user);
        when(userRepository.save(userWithoutId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto actual= userService.create(givenNewUserDto);

        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userMapper,userRepository);
        assertThat(actual).hasNoNullFieldsOrPropertiesExcept("id");
    }
    @Test
    void givenNewUserDto_whenSavingError_thenThrowUserCreationException(){
        NewUserDto givenNewUserDto = new NewUserDto(user.getUsername(), user.getFirstname(),
                user.getUsername(), user.getPassword(), DATE);
        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(false);
        when(userMapper.toUserFromNewUserDto(givenNewUserDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(null);

        verifyNoMoreInteractions(userMapper,userRepository);
        assertThatExceptionOfType(EntityCreationException.class)
                .isThrownBy(()-> userService.create(givenNewUserDto))
                .withMessage("User 'testUser' creation error");
    }
    @Test
    void givenNewUserDto_whenUsernameAlreadyExists_thenThrowUsernameAlreadyExistsException(){
        NewUserDto givenNewUserDto = new NewUserDto(user.getUsername(), user.getFirstname(),
                user.getUsername(), user.getPassword(), DATE);
        when(userRepository.existsUserByUsername(user.getUsername())).thenReturn(true);

        assertThatExceptionOfType(UsernameAlreadyExistsException.class)
                .isThrownBy(()-> userService.create(givenNewUserDto))
                .withMessage("The username testUser is already exists");
    }

    @Test
    void givenId_whenExistAndDelete_thenRDelete(){
        when(userRepository.findUserById(ID)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        doNothing().when(userRepository).flush();

        userService.delete(ID);

        verify(userRepository, times(1)).delete(user);
        verify(userRepository, times(1)).flush();
        verifyNoMoreInteractions(userRepository);

    }

    @Test
    void givenId_whenNotFound_thenThrowNotFoundException(){
        when(userRepository.findUserById(ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(()->userService.delete(ID))
                .withMessage("The user '%1$s' is not found".formatted(ID));

        verify(userRepository, times(1)).findUserById(ID);
        verify(userRepository, never()).delete(any(User.class));
        verify(userRepository, never()).flush();
        verifyNoMoreInteractions(userRepository);
    }


}