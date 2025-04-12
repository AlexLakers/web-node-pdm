package com.alex.web.node.pdm.api.rest;

import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.model.Specification;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.DetailService;
import com.alex.web.node.pdm.service.SpecificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(RestSpecificationController.class)
@RequiredArgsConstructor
@WithMockUser(username = "admin", authorities = {"ADMIN"}, password = "password")
class RestSpecificationControllerTest {

    private final static Long ID = 1L;
    private final NewSpecificationDto newSpecificationDto = new NewSpecificationDto("testCode", "testDesc");
    private final SpecificationDto specificationDto = new SpecificationDto(ID, "testCode", 100, "testDesc", ID, Collections.singletonList(1L));
    private final Specification specification = Specification.builder().id(ID).desc("testDesc").code("testCode").build();
    private final MockMvc mockMvc;
    @MockitoBean
    private final SpecificationService specificationService;
   @MockitoBean
    private final DetailService detailService;
    private final ObjectMapper objectMapper;


    @Test
    @SneakyThrows
    void givenNewSpecificationDto_whenCreateSpec_thenReturnStatusCreatedAndNotEmptyBody() {
        Mockito.when(specificationService.create(newSpecificationDto)).thenReturn(specificationDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/specifications")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSpecificationDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(specificationDto)));
    }

    @Test
    @SneakyThrows
    void givenUpdateSpecDtoAndId_whenUSpecUpdate_whenStatusOkAndNotEmptyBody() {
        final String POSTFIX_UPDATE = "update";
        UpdateSpecificationDto givenUpdateSpecDto = new UpdateSpecificationDto(specification.getCode() + POSTFIX_UPDATE, specification.getDesc() + POSTFIX_UPDATE);
        SpecificationDto expected = new SpecificationDto(ID, givenUpdateSpecDto.code(), specificationDto.amount(), givenUpdateSpecDto.desc(), ID, specificationDto.detailsIds());
        Mockito.when(specificationService.update(ID, givenUpdateSpecDto)).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/specifications/{id}", ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(givenUpdateSpecDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detailsIds", Matchers.contains(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", Matchers.is(givenUpdateSpecDto.code())));

/*}.andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(user.getUsername())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.contains(RoleName.ADMIN.name())));*/
    }

    @Test
    @SneakyThrows
    public void givenNotFoundId_whenSpecNotFoundDuringUpdate_thenReturnStatusNotFound() {
        UpdateSpecificationDto givenUpdateSpecDto = new UpdateSpecificationDto(specification.getCode(), specification.getDesc());
        Mockito.when(specificationService.update(Mockito.anyLong(), Mockito.any(UpdateSpecificationDto.class))).thenThrow(new EntityNotFoundException("The spec with id '%1$d' is not found".formatted(ID)));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/specifications/{id}", ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(givenUpdateSpecDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void findAll_thenReturnStatusOkAnd() {
        SpecificationSearchDto searchDto = new SpecificationSearchDto(ID, "testCode", 0, 2, "ASC", "code");
        Page<SpecificationDto> expected=new PageImpl<>(Collections.singletonList(specificationDto));
        Mockito.when(specificationService.findAll(searchDto)).thenReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/specifications")
                        .param("userId", "1")
                        .param("code","testCode")
                        .param("pageNumber","0")
                        .param("pageSize","20")
                        .param("sortDirection","ASC")
                        .param("sortColumn","id")
                        .characterEncoding(StandardCharsets.UTF_8)

                )
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
    @Test
    @SneakyThrows
    void givenId_whenDelete_thenReturnStatusNotContent(){
        Mockito.doNothing().when(specificationService).delete(ID);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/specifications/{id}",ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    @Test
    @SneakyThrows
    public void givenNotFoundId_whenUserNotFoundDuringDelete_thenReturnStatusNotFound() {
        Mockito.doThrow(new EntityNotFoundException("The spec with id '%1$d' is not found".formatted(ID))).when(specificationService).delete(Mockito.anyLong());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/specifications/{id}",ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    @SneakyThrows
    void givenId_whenUserFound_thenReturnStatusOkAndNotEmptyBody() {
        Mockito.when(specificationService.findById(ID)).thenReturn(specificationDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/specifications/{id}",ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(specificationDto)));

    }


}