package com.alex.web.node.pdm.api.rest;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.specification.NewSpecificationDto;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.specification.UpdateSpecificationDto;
import com.alex.web.node.pdm.search.SpecificationSearchDto;
import com.alex.web.node.pdm.service.DetailService;
import com.alex.web.node.pdm.service.SpecificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/specifications")
@RequiredArgsConstructor
public class RestSpecificationController {
    private final SpecificationService specificationService;
    private final DetailService detailService;

    @GetMapping
    public ResponseEntity<Page<SpecificationDto>> findAll(/*@RequestParam(defaultValue = "0") Integer pageNumber,
                                                          @RequestParam(defaultValue = "20") Integer pageSize,*/
            SpecificationSearchDto specificationSearchDto/*SpecificationSearch specificationSearch,
                                                          @PageableDefault(page = 0,size = 20)
                                                          @SortDefault(sort = "id",direction = Sort.Direction.ASC) Pageable pageable*/) {
        log.info("--start 'find all specifications' rest endpoint--");
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .body(specificationService.findAll(specificationSearchDto));
    }
    @GetMapping("/{specId}/details")
    public ResponseEntity<List<DetailDto>> findAllDetailsBySpecId(@PathVariable Long specId){
        log.info("--start 'find all detail by specification id' rest endpoint--");
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(detailService.findAllBySpecId(specId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<SpecificationDto> findById(@PathVariable("id") Long id) {
        log.info("--start 'find specification by id' rest endpoint--");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(specificationService.findById(id));
    }
  /*  @GetMapping
    public ResponseEntity<SpecificationDto> findByCode(@RequestParam("code") String code) {
        return ResponseEntity.status(HttpStatus.OK).body(specificationService.findByCode(code));
    }*/

    /* @GetMapping
     public ResponseEntity<List<SpecificationDto>> findByUserId(@RequestParam("userId") Long userId) {
         return ResponseEntity.status(HttpStatus.OK).body(specificationService.findAllByUserId(userId));
     }*/
    @PostMapping
    public ResponseEntity<SpecificationDto> create(@RequestBody @Validated NewSpecificationDto newSpecificationDto){
        log.info("--start 'create a new specification' rest endpoint--");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .body(specificationService.create(newSpecificationDto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<SpecificationDto> update(@PathVariable Long id,
                                                   @RequestBody @Validated UpdateSpecificationDto updateSpecificationDto){
        log.info("--start 'update specification by id' rest endpoint--");
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_VALUE)
                .body(specificationService.update(id,updateSpecificationDto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<SpecificationDto> delete(@PathVariable Long id){
        log.info("--start 'delete specification by id' rest endpoint--");
        specificationService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
