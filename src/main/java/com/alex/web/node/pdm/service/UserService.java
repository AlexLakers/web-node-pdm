package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    Optional<String> getCurrentName();

    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto update(Long id, UpdateUserDto updateUserDto);

    UserDto create(NewUserDto newUserDto);

    void delete(Long id);

}
