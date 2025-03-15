package com.alex.web.node.pdm.api.rest;

import com.alex.web.node.pdm.config.CustomUserDetails;
import com.alex.web.node.pdm.dto.specification.SpecificationDto;
import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class RestUserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Validated @RequestBody NewUserDto newUserDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(newUserDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> findById(@PathVariable Long id,
                                            @AuthenticationPrincipal CustomUserDetails authUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findById(id));


    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok().body(users);

    }
    @GetMapping("/{userId}/specifications")
    @PreAuthorize("#authUser.id==#userId OR hasAuthority('ADMIN')")
    public ResponseEntity<List<SpecificationDto>> findAllSpecificationsByUserId(@PathVariable Long userId,
                                                                                @AuthenticationPrincipal CustomUserDetails authUser) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(specificationService.findAllByUserId(userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> update(@PathVariable Long id,
                                          @RequestBody UpdateUserDto updateUserDto,
                                          @AuthenticationPrincipal CustomUserDetails authUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.update(id, updateUserDto));

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#authUser.id==#id OR hasAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails authUser) {
        userService.delete(id);
        return ResponseEntity.noContent().build();

    }


}
