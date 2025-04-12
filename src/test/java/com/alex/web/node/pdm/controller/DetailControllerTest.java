package com.alex.web.node.pdm.controller;


import com.alex.web.node.pdm.config.security.CustomUserDetails;
import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.service.DetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.With;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(DetailController.class)
@RequiredArgsConstructor
@WithMockUser(username = "test@mail.com", authorities = {"ADMIN", "USER"}, password = "password")
class DetailControllerTest {
    private final static Long ID = 1L;
    private final static Long INVALID_ID = 1000000L;
    @MockitoBean
    private final DetailService detailService;
    private final MockMvc mockMvc;
    private final DetailDto detailDto = new DetailDto(ID, "testDetail", 100, ID);
    private  final CustomUserDetails authUserWithId=new CustomUserDetails("admin","pass", List.of(new SimpleGrantedAuthority("ADMIN")),ID);


    @Test
    @SneakyThrows
    void givenNewDetailDto_whenCreateDetail_thenSetModelAndView() {
        NewDetailDto newDetailDto = new NewDetailDto("NewDetail", 1000, ID);
        Mockito.when(detailService.create(newDetailDto)).thenReturn(detailDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/details")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", newDetailDto.name())
                        .param("amount", String.valueOf(newDetailDto.amount()))
                        .param("specificationId", String.valueOf(ID)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/specifications/" + ID + "/details"));
    }

    @Test
    @SneakyThrows
    void givenNotValidNewDetailDto_whenDetailNotValidatedDuringCreate_thenSetModelAndViewRedirectSpecsPage() {
        NewDetailDto newDetailDto = new NewDetailDto("NewDetail", 1000, ID);
        Mockito.when(detailService.create(newDetailDto)).thenReturn(detailDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/details")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "")
                        .param("amount", String.valueOf(newDetailDto.amount()))
                        .param("specificationId", String.valueOf(ID)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/specifications/" + ID + "/details"));

    }

    @Test
    @SneakyThrows
    void givenUpdateDetailDtoAndId_whenUpdateDetail_thenSetModelAndViewDetailsPage() {
        UpdateDetailDto updateDetailDto = new UpdateDetailDto("UpdatedName", 100);
        Mockito.when(detailService.update(ID, updateDetailDto)).thenReturn(detailDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/details/{id}/update", ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(authUserWithId))
                        .param("name", updateDetailDto.name())
                        .param("amount", String.valueOf(updateDetailDto.amount())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("detail/detail"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("detail"));
    }

    @Test
    @SneakyThrows
    void givenNotFoundId_whenDetailNotFound_thenRedirectErrorPage() {

        Mockito.when(detailService.update(Mockito.anyLong(), Mockito.any(UpdateDetailDto.class))).
                thenThrow(new EntityNotFoundException("The spec with id '%1$d' is not found".formatted(INVALID_ID)));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/details/{id}/update", INVALID_ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.user(authUserWithId))
                        .param("name", "name")
                        .param("amount", String.valueOf(1000)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.view().name("error/error"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("error"));
    }

    @Test
    @SneakyThrows
    void givenNotValidUpdateDetailDto_whenNotValidated_thenRedirectErrorPage() {
        Mockito.when(detailService.update(Mockito.anyLong(), Mockito.any(UpdateDetailDto.class))).thenReturn(detailDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/details/{id}/update", ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "")
                        .param("amount", String.valueOf(1000)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/details/"+ID));
    }

}