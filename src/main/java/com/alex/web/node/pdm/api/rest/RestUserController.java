package com.alex.web.node.pdm.api.rest;

import com.alex.web.node.pdm.config.security.CustomUserDetails;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import com.alex.web.node.pdm.service.SpecificationService;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;
    private final SpecificationService specificationService;

    /**
     * Returns status and output-dto formatted .JSON of created entity.
     *
     * @param newUserDto input-dto.
     * @return output-dto.
     */

    @PostMapping
    public ResponseEntity<UserDto> create(@Validated @RequestBody NewUserDto newUserDto) {
        log.info("--start 'create a new user' rest endpoint--");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(newUserDto));
    }

    /**
     * Returns status and output-dto formatted .JSON by id of user.
     *
     * @param id       id of user.
     * @param authUser current logged user from security context.
     * @return output-dto.
     */

    @GetMapping("/{id}")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> findById(@PathVariable Long id,
                                            @AuthenticationPrincipal CustomUserDetails authUser) {
        log.info("--start 'find user by id' rest endpoint--");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findById(id));


    }

    /**
     * Returns status and list of output-dto formatted .JSON without any criteria.
     *
     * @return list of output-dto.
     */

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        log.info("--start 'find all specifications  for user by id' rest endpoint--");
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok().body(users);

    }
    /**
     * Returns status and list of output-specification-dto formatted .JSON by id of user.
     *
     * @param userId   id of user.
     * @param authUser current logged user from security context.
     * @return output dto of specification.
     */

    @GetMapping("/{userId}/specifications")
    @PreAuthorize("#authUser.id==#userId OR hasAuthority('ADMIN')")
    public ResponseEntity<List<SpecificationDto>> findAllSpecificationsByUserId(@PathVariable Long userId,
                                                                                @AuthenticationPrincipal CustomUserDetails authUser) {
        log.info("--start 'find all users' rest endpoint--");
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(specificationService.findAllByUserId(userId));
    }

    /**
     * Returns status and updated output-dto formatted .JSON by input-dto and id of user.
     *
     * @param id            id of user.
     * @param updateUserDto input-dto.
     * @param authUser      current logged user from security context.
     * @return output-dto.
     */

    @PutMapping("/{id}")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                          @RequestBody UpdateUserDto updateUserDto,
                                          @AuthenticationPrincipal CustomUserDetails authUser) {
        log.info("--start 'update user by id' rest endpoint--");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.update(id, updateUserDto));

    }

    /**
     * Returns status of deleting operation.
     *
     * @param id       if of user.
     * @param authUser current logged user from security context.
     * @return http-status.
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails authUser) {
        log.info("--start 'delete user by id' rest endpoint--");
        userService.delete(id);
        return ResponseEntity.noContent().build();

    }


}
