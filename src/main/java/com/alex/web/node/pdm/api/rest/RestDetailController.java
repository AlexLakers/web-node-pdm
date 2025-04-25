package com.alex.web.node.pdm.api.rest;


import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.service.DetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/details")
@RequiredArgsConstructor
public class RestDetailController {
    private final DetailService detailService;

    @GetMapping("/{id}")
    public ResponseEntity<DetailDto> findById(@PathVariable Long id) {
        log.info("--start 'find detail by id' rest endpoint--");
        return ResponseEntity.status(HttpStatus.OK).body(detailService.findById(id));
    }


    @GetMapping
    public ResponseEntity<Page<DetailDto>> findAllByPageable(@PageableDefault(page = 0, size = 20)
                                                             @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                                             Pageable pageable) {
        log.info("--start 'find all detail by pageable' rest endpoint--");
        return ResponseEntity.ok().body(detailService.findAllByPageable(pageable));
    }


    @PostMapping
    public ResponseEntity<DetailDto> create(@Validated @RequestBody NewDetailDto newDetailDto) {
        log.info("--start 'create a new detail' rest endpoint--");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(detailService.create(newDetailDto));
    }



    @PutMapping("/{id}")
    public ResponseEntity<DetailDto> update(@PathVariable Long id,
                                            @Validated @RequestBody UpdateDetailDto updateDetailDto) {
        log.info("--start 'update detail by id' rest endpoint--");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(detailService.update(id, updateDetailDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("--start 'delete user by id' rest endpoint--");
        detailService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
