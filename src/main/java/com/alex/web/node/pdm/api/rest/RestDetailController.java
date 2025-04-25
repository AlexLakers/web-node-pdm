package com.alex.web.node.pdm.api.rest;


import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.model.Detail;
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

/**
 * This is a REST-controller.It provides set of endpoints for using the main operation with {@link Detail detail}.
 */

@Slf4j
@RestController
@RequestMapping("api/v1/details")
@RequiredArgsConstructor
public class RestDetailController {
    private final DetailService detailService;

    /**
     * Returns status and output-dto formatted .JSON by id of detail.
     *
     * @param id of detail.
     * @return output-dto.
     */

    @GetMapping("/{id}")
    public ResponseEntity<DetailDto> findById(@PathVariable Long id) {
        log.info("--start 'find detail by id' rest endpoint--");
        return ResponseEntity.status(HttpStatus.OK).body(detailService.findById(id));
    }

    /**
     * Returns status and page of output-dto formatted .JSON by pageable
     *
     * @param pageable input-pageable.
     * @return page of output-dto.
     */

    @GetMapping
    public ResponseEntity<Page<DetailDto>> findAllByPageable(@PageableDefault(page = 0, size = 20)
                                                             @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                                             Pageable pageable) {
        log.info("--start 'find all detail by pageable' rest endpoint--");
        return ResponseEntity.ok().body(detailService.findAllByPageable(pageable));
    }

    /**
     * Returns status and output-dto formatted .JSON of created entity.
     *
     * @param newDetailDto input-dto.
     * @return output-dto.
     */

    @PostMapping
    public ResponseEntity<DetailDto> create(@Validated @RequestBody NewDetailDto newDetailDto) {
        log.info("--start 'create a new detail' rest endpoint--");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(detailService.create(newDetailDto));
    }


    /**
     * Returns status and updated output-dto formatted .JSON by input-dto and id of detail.
     *
     * @param id              id of detail.
     * @param updateDetailDto input-dto.
     * @return output-dto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DetailDto> update(@PathVariable Long id,
                                            @Validated @RequestBody UpdateDetailDto updateDetailDto) {
        log.info("--start 'update detail by id' rest endpoint--");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(detailService.update(id, updateDetailDto));
    }
    /**
     * Returns status of deleting operation.
     *
     * @param id of detail.
     * @return output-dto.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("--start 'delete user by id' rest endpoint--");
        detailService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
