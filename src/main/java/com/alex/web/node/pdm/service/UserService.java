package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.dto.UserDto;

import java.util.List;

public interface UserService{

    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto update(Long id, UpdateUserDto updateUserDto);

    UserDto create(NewUserDto newUserDto);

    void delete(Long id);

}
