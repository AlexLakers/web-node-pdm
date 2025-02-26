package com.alex.web.node.pdm.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(LoginController.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@WithMockUser(username = "test@mail.com", authorities = {"ADMIN", "USER"}, password = "password")
class LoginControllerTest {

    private final MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testLogin_thenLoginPage() {
        mockMvc.perform(MockMvcRequestBuilders.get("/login")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.TEXT_HTML)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

}