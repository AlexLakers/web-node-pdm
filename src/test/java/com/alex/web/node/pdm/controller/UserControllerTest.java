package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.config.security.CustomUserDetails;
import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.Provider;
import com.alex.web.node.pdm.model.enums.RoleName;
import com.alex.web.node.pdm.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@WithMockUser
class UserControllerTest {
    @MockitoBean
    private final UserServiceImpl userService;

    private final MockMvc mockMvc;

    private final Long ID = 1L;
    private static final LocalDate DATE = LocalDate.of(1993, 1, 1);
    private final User user = User.builder().id(ID).firstname("testUser123").lastname("testUser123")
            .birthday(LocalDate.of(1993, 1, 1))
            .password("Lakers123")
            .roles(Collections.singletonList(Role.builder().roleName(RoleName.ADMIN).build()))
            .username("Lakers@mail")
            .build();

    private  final CustomUserDetails authUserWithId=new CustomUserDetails("admin","pass",List.of(new SimpleGrantedAuthority("ADMIN")),ID);
    private final List<String> roles = Collections.singletonList("ADMIN");
    private final UserDto userDto = new UserDto(ID, user.getUsername(), user.getFirstname(), user.getLastname(), DATE, roles, Provider.DAO_LOCAL.name());


    @Test
    @SneakyThrows
    void givenNewUserDto_whenCreateUser_thenRedirectLoginPage() {
        NewUserDto givenNewUserDto = new NewUserDto(user.getFirstname(),
                user.getLastname(), user.getUsername(), user.getPassword(), DATE);
        Mockito.when(userService.create(givenNewUserDto)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.TEXT_HTML)
                        .param(NewUserDto.Fields.firstname, givenNewUserDto.firstname())
                        .param(NewUserDto.Fields.lastname, givenNewUserDto.lastname())
                        .param(NewUserDto.Fields.username, givenNewUserDto.username())
                        .param(NewUserDto.Fields.birthday, givenNewUserDto.birthday().format(DateTimeFormatter.ISO_DATE))
                        .param(NewUserDto.Fields.password, givenNewUserDto.password()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));

    }
    @Test
    @SneakyThrows
    void givenNotValidNewUserDto_whenUserNotValidatedDuringCreate_thenSetModelAndViewAndRedirectRegistrationPage() {
        NewUserDto givenNewUserDto = new NewUserDto(user.getFirstname(),
                user.getLastname(), user.getUsername(), user.getPassword(), DATE);
        Mockito.when(userService.create(givenNewUserDto)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.TEXT_HTML)
                        .param(NewUserDto.Fields.firstname, "")
                        .param(NewUserDto.Fields.lastname, "")
                        .param(NewUserDto.Fields.username, "")
                        .param(NewUserDto.Fields.birthday, "")
                        .param(NewUserDto.Fields.password, ""))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/registration"));


    }



    @Test
    @SneakyThrows
    void givenUpdateUserDtoAndId_whenUpdateUser_thenSetModelAndViewAndRedirectUserPage() {
        final String POSTFIX_UPDATE = "update";
        UpdateUserDto givenUpdateUserDto = new UpdateUserDto(user.getFirstname() + POSTFIX_UPDATE,
                user.getLastname() + POSTFIX_UPDATE, user.getUsername() + POSTFIX_UPDATE, DATE);
        UserDto userDto = (new UserDto(ID, user.getUsername() + POSTFIX_UPDATE,
                user.getFirstname() + POSTFIX_UPDATE, user.getLastname() + POSTFIX_UPDATE, DATE, null, null));
        Mockito.when(userService.update(ID, givenUpdateUserDto)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/" + ID + "/update")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.TEXT_HTML)
                        .param(UpdateUserDto.Fields.firstname, givenUpdateUserDto.firstname())
                        .param(UpdateUserDto.Fields.lastname, givenUpdateUserDto.lastname())
                        .param(UpdateUserDto.Fields.username, givenUpdateUserDto.username())
                        .param(UpdateUserDto.Fields.birthday, givenUpdateUserDto.birthday().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/" + ID));
    }



    @Test
    @SneakyThrows
    void givenNotValidUpdateUserDto_whenNotValidated_thenRedirectErrorPage() {
        final String POSTFIX_UPDATE = "update";
        UserDto userDto = (new UserDto(ID, user.getUsername() + POSTFIX_UPDATE,
                user.getFirstname() + POSTFIX_UPDATE, user.getLastname() + POSTFIX_UPDATE, DATE, null, null));
        Mockito.when(userService.update(Mockito.anyLong(), Mockito.any(UpdateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/" + ID + "/update")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.TEXT_HTML)
                        .param(UpdateUserDto.Fields.firstname, "")
                        .param(UpdateUserDto.Fields.lastname, "")
                        .param(UpdateUserDto.Fields.username, "")
                        .param(UpdateUserDto.Fields.birthday, ""))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users/" + ID));
    }

    @Test
    @SneakyThrows
    void findAll_thenSetModelAndView() {
        Mockito.when(userService.findAll()).thenReturn(List.of(userDto));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .with(SecurityMockMvcRequestPostProcessors.user(authUserWithId))
                        .accept(MediaType.TEXT_HTML)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("users", List.of(userDto)));
    }

    @Test
    @SneakyThrows
    void givenId_whenDelete_thenRedirectUsersPage() {
        Mockito.doNothing().when(userService).delete(ID);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/delete")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .param("userId", ID + ""))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/users"));
    }

    @Test
    @SneakyThrows
    void givenId_whenUserFound_thenUserPage(){
        Mockito.when(userService.findById(ID)).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(authUserWithId))
                        .header("referer","")
                        .accept(MediaType.TEXT_HTML)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto))
                .andExpect(MockMvcResultMatchers.view().name("user/user"));
    }

}