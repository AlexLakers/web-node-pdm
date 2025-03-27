package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.config.security.CustomUserDetails;
import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.model.Detail;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.DetailService;
import com.alex.web.node.pdm.service.SpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecificationController.class)
@RequiredArgsConstructor
@WithMockUser(username = "test@mail.com", authorities = {"ADMIN", "USER"}, password = "password")
class SpecificationControllerTest {

    @MockitoBean
    private final SpecificationService specificationService;
    @MockitoBean
    private final DetailService detailService;
    private final MockMvc mockMvc;
    private static final Long ID = 1L;
    private static final Long INVALID_ID = Long.MAX_VALUE;
    private final User user = User.builder().id(1L).username("testUser").build();
    private  final CustomUserDetails authUserWithId=new CustomUserDetails("admin","pass",List.of(new SimpleGrantedAuthority("ADMIN")),ID);
    private final List<Detail> details = List.of(Detail.builder().name("testName").id(ID).amount(500).build());
    private final Specification specification = Specification.builder()
            .amount(1000).desc("desc").code("testCode").user(user).details(details).build();
    private final SpecificationDto specificationDto = new SpecificationDto(ID, "testCode", 1000, "desc", ID, List.of(ID));
    private final List<Specification> specifications = List.of(specification);

    @Test
    @SneakyThrows
    void givenNewSpecDto_whenCreate_thenReturnSpecDtoWithId() {

        NewSpecificationDto requestBody = new NewSpecificationDto("NEWtestCode", "NEWdesc");
        when(specificationService.create(requestBody)).thenReturn(specificationDto);

        mockMvc.perform(post("/specifications")
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", requestBody.code())
                        .param("desc", requestBody.desc()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/specifications"));

    }

    @Test
    @SneakyThrows
    void givenNotValidNewSpecDto_whenSpecNotValidatedDuringCreate_thenSetModelAndViewAndRedirectSpecificationPage() {
        NewSpecificationDto requestBody = new NewSpecificationDto("NEWtestCode", "NEWdesc");
        when(specificationService.create(requestBody)).thenReturn(specificationDto);

        mockMvc.perform(post("/specifications")
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", "")
                        .param("desc", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/specifications"));
    }

    @Test
    @SneakyThrows
    void givenUpdateSpecDtoAndId_whenUpdateSpec_thenSetModelAndViewSpecificationPage() {
        UpdateSpecificationDto givenUpdateSpecDto = new UpdateSpecificationDto("UPDcode", "UPDdesc");
        when(specificationService.update(ID, givenUpdateSpecDto)).thenReturn(specificationDto);

        mockMvc.perform(post("/specifications/" + ID + "/update")
                        .with(csrf())
                        .with(user(authUserWithId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", givenUpdateSpecDto.code())
                        .param("desc", givenUpdateSpecDto.desc()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("spec"))
                .andExpect(view().name("specification/specification"));

    }

    @Test
    @SneakyThrows
    void givenNotFoundId_whenSpecNotFound_thenRedirectErrorPage() {
        UpdateSpecificationDto givenUpdateSpecDto = new UpdateSpecificationDto("UPDcode", "UPDdesc");
        when(specificationService.update(anyLong(), any(UpdateSpecificationDto.class)))
                .thenThrow(new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(INVALID_ID)));

        mockMvc.perform(post("/specifications/" + INVALID_ID + "/update")
                        .with(csrf())
                        .with(user(authUserWithId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", givenUpdateSpecDto.code())
                        .param("desc", givenUpdateSpecDto.desc()))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("error/error"));

    }

    @Test
    @SneakyThrows
    void givenNotValidUpdateSpecDto_whenNotValidated_thenRedirectErrorPage() {

        when(specificationService.update(anyLong(), any(UpdateSpecificationDto.class))).thenReturn(specificationDto);

        mockMvc.perform(post("/specifications/" + ID + "/update")
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", "")
                        .param("desc", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/specifications/" + ID));
    }

    @Test
    @SneakyThrows
    void givenSpecSearchDto_whenFindSpec_thenSetModelAndView() {
        SpecificationSearchDto givenSearchDto = new SpecificationSearchDto(ID, "testCode", 0, 2, "ASC", "code");
        Page<SpecificationDto> expected = new PageImpl<>(Collections.singletonList(specificationDto), PageRequest.of(0, 2), 1);
        CustomUserDetails authUserWithId=new CustomUserDetails("admin","pass",List.of(new SimpleGrantedAuthority("ADMIN")),ID);
        when(specificationService.findAll(givenSearchDto)).thenReturn(expected);

        mockMvc.perform(get("/specifications")
                        .with(csrf())
                        .with(user(authUserWithId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("userId", String.valueOf(givenSearchDto.userId()))
                        .param("code", givenSearchDto.code())
                        .param("pageSize", String.valueOf(givenSearchDto.pageSize()))
                        .param("pageNumber", String.valueOf(givenSearchDto.pageNumber()))
                        .param("orderColumn", String.valueOf(givenSearchDto.orderColumn()))
                        .param("orderDirection", String.valueOf(givenSearchDto.orderDirection())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page", "search"));

    }

    @Test
    @SneakyThrows
    void givenId_whenDelete_thenRedirectSpecificationsPage() {
        doNothing().when(specificationService).delete(ID);

        mockMvc.perform(post("/specifications/delete")
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("specId", String.valueOf(ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/specifications"));
    }

    @Test
    @SneakyThrows
    void givenNotFoundId_whenNotFound_thenThrowEntityNotFoundException() {
        doThrow(new EntityNotFoundException("The spec with id '%1$s' is not found".formatted(INVALID_ID)))
                .when(specificationService).delete(INVALID_ID);

        mockMvc.perform(post("/specifications/delete")
                        .with(csrf())
                        .with(user(authUserWithId))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("specId", String.valueOf(INVALID_ID)))
                .andExpect(status().isNotFound())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("error/error"));

    }

    @Test
    @SneakyThrows
    void givenId_whenFoundDetails_thenSetModelAndView() {
        List<DetailDto> expected = List.of(new DetailDto(ID,"name",100,ID));
        when(detailService.findAllBySpecId(ID)).thenReturn(expected);

        mockMvc.perform(get("/specifications/{specId}/details", ID)
                        .with(csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.TEXT_HTML)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("specId", String.valueOf(ID)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("details"))
                .andExpect(view().name("detail/details"));
    }


}