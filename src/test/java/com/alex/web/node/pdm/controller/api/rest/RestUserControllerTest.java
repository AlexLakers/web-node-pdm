package com.alex.web.node.pdm.controller.api.rest;

import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.Provider;
import com.alex.web.node.pdm.model.enums.RoleName;
import com.alex.web.node.pdm.api.rest.RestUserController;
import com.alex.web.node.pdm.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(RestUserController.class)
/*@ContextConfiguration(classes = RestErrorHandler.class)*/
/*@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)*/
@RequiredArgsConstructor
@WithMockUser(username = "admin", authorities = {"ADMIN"}, password = "password")
class RestUserControllerTest {

    @MockitoBean
    private final UserService userService;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final Long ID = 1L;
    private static final LocalDate DATE = LocalDate.of(1993, 1, 1);
    private final User user = User.builder().id(ID).firstname("testUser123").lastname("testUser123")
            .birthday(LocalDate.of(1993, 1, 1))
            .password("Lakers123")
            .roles(Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build()))
            .username("Lakers@mail")
            .build();

    private final List<String> roles = Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build().getAuthority());
    private final UserDto userDto = new UserDto(ID, user.getUsername(), user.getFirstname(), user.getLastname(), DATE, roles, Provider.DAO_LOCAL.name());


    @Test
    @SneakyThrows
    void givenNewUserDto_whenCreateUser_thenReturnStatusCreatedAndNotEmptyBody() {
        NewUserDto givenNewUserDto = new NewUserDto(user.getFirstname(),
                user.getLastname(), user.getUsername(), user.getPassword(), DATE);
        when(userService.create(givenNewUserDto)).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/users")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenNewUserDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(user.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.contains(RoleName.ADMIN.name())));

    }



    @Test
    @SneakyThrows
    void givenUpdateUserDtoAndId_whenUserUpdate_whenStatusOkAndNotEmptyBody() {
        final String POSTFIX_UPDATE = "update";
        UpdateUserDto givenUpdateUserDto = new UpdateUserDto(user.getFirstname() + POSTFIX_UPDATE,
                user.getLastname() + POSTFIX_UPDATE, user.getUsername() + POSTFIX_UPDATE, DATE);
        UserDto userDto = (new UserDto(ID, user.getUsername() + POSTFIX_UPDATE,
                user.getFirstname() + POSTFIX_UPDATE, user.getLastname() + POSTFIX_UPDATE, DATE, null, null));
        when(userService.update(ID, givenUpdateUserDto)).thenReturn(userDto);
        String body = objectMapper.writeValueAsString(givenUpdateUserDto);

        mockMvc.perform(put("/api/v1/users/" + ID)
                        //add csrf token to request because my custom SecurityConfig is not downloaded then in test used
                        //default SecurityFilterChain where csrf.enable
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(body));

    }
    @Test
    @SneakyThrows
    public void givenNotFoundId_whenUserNotFoundDuringUpdate_thenReturnStatusNotFound() {
        UpdateUserDto givenUpdateUserDto = new UpdateUserDto(user.getFirstname(),
                user.getLastname() , user.getUsername(), DATE);
        when(userService.update(anyLong(), any(UpdateUserDto.class))).thenThrow(new EntityNotFoundException("The user '%1$s' is not found".formatted(ID)));

        mockMvc.perform(put("/api/v1/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(givenUpdateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @SneakyThrows
    void findAll_thenReturnStatusOkAnd() {
        List<String> roles = Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build().getAuthority());
        List<UserDto> users = Arrays.asList(new UserDto(ID, user.getUsername(), user.getFirstname(), user.getLastname(), DATE, roles, Provider.DAO_LOCAL.name()),
                new UserDto(ID + 1L, user.getUsername(), user.getFirstname(), user.getLastname(), DATE, roles, Provider.DAO_LOCAL.name()));
        when(userService.findAll()).thenReturn(users);
        String body = objectMapper.writeValueAsString(users);
        mockMvc.perform(get("/api/v1/users")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(body));

    }

    @Test
    @SneakyThrows
    void givenId_whenDelete_thenReturnStatusNotContent() {
        doNothing().when(userService).delete(ID);

        mockMvc.perform(delete("/api/v1/users/{id}",ID)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @SneakyThrows
    public void givenNotFoundId_whenUserNotFoundDuringDelete_thenReturnStatusNotFound() {
        doThrow(new EntityNotFoundException("The user '%1$s' is not found".formatted(ID))).when(userService).delete(anyLong());

        mockMvc.perform(delete("/api/v1/users/{id}",ID)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @SneakyThrows
    void givenId_whenUserFound_thenReturnStatusOkAndNotEmptyBody() {

        UserDto userDto = new UserDto(ID + 1L, user.getUsername(), user.getFirstname(), user.getLastname(),
                DATE, roles, Provider.DAO_LOCAL.name());
        when(userService.findById(ID)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/{id}",ID)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userDto)));
    }

}