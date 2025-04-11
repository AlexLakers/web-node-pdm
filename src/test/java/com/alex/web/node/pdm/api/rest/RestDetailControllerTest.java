package com.alex.web.node.pdm.api.rest;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.service.DetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(RestDetailController.class)
@RequiredArgsConstructor
@WithMockUser(username = "admin", authorities = {"ADMIN"}, password = "password")
class RestDetailControllerTest {
    private final static Long ID = 1L;
    private final static Long INVALID_ID = 1000000L;
    @MockitoBean
    private final DetailService detailService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DetailDto detailDto = new DetailDto(ID, "testDetail", 100, ID);

    @Test
    @SneakyThrows
    void givenNewDetailDto_whenCreateDetail_thenReturnStatusCreatedAndNotEmptyBody() {
        NewDetailDto givenNewDetailDto = new NewDetailDto("NewDetail", 100, 1L);
        Mockito.when(detailService.create(givenNewDetailDto)).thenReturn(detailDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/details")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenNewDetailDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(detailDto)));
    }

    @Test
    @SneakyThrows
    void givenUpdateSpecDtoAndId_whenUSpecUpdate_whenStatusOkAndNotEmptyBody() {
        UpdateDetailDto givenUpdateDetailDto = new UpdateDetailDto("updatedName", 100);
        Mockito.when(detailService.update(ID, givenUpdateDetailDto)).thenReturn(detailDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/details/{id}", ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenUpdateDetailDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(detailDto.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.is(detailDto.amount())));
    }

    @Test
    @SneakyThrows
    public void givenNotFoundId_whenSpecNotFoundDuringUpdate_thenReturnStatusNotFound() {
        UpdateDetailDto givenUpdateDetailDto = new UpdateDetailDto("updatedName", 100);
        Mockito.when(detailService.update(INVALID_ID, givenUpdateDetailDto))
                .thenThrow((new EntityNotFoundException("The detail with id '%1$d' is not found".formatted(INVALID_ID))));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/details/{id}", INVALID_ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenUpdateDetailDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @SneakyThrows
    void givenId_whenDelete_thenReturnStatusNotContent() {
        Mockito.when(detailService.delete(ID)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/details/{id}", ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    @Test
    @SneakyThrows
    void givenNotValidId_whenDelete_thenReturnStatusNotFound() {
        Mockito.when(detailService.delete(INVALID_ID))
                .thenThrow((new EntityNotFoundException("The detail with id '%1$d' is not found".formatted(INVALID_ID))));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/details/{id}", INVALID_ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}