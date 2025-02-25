package com.alex.web.node.pdm.service.impl;

import com.alex.web.node.pdm.dto.NewUserDto;
import com.alex.web.node.pdm.dto.UpdateUserDto;
import com.alex.web.node.pdm.dto.UserDto;
import com.alex.web.node.pdm.exception.EntityCreationException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.exception.UsernameAlreadyExistsException;
import com.alex.web.node.pdm.mapper.user.UserMapper;
import com.alex.web.node.pdm.repository.UserRepository;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserDto findById(Long id) {
        return userRepository.findUserById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("The user '%1$s' is not found".formatted(id)));
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto update(Long id, UpdateUserDto updateUserDto) {
        return userRepository.findUserById(id)
                .map(user -> {
                    userMapper.updateUser(user, updateUserDto);
                    return userRepository.saveAndFlush(user);
                })
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("The user '%1$s' is not found".formatted(id)));

    }

    @Transactional
    public UserDto create(NewUserDto newUserDto) {
        if (userRepository.existsUserByUsername(newUserDto.username()))
            throw new UsernameAlreadyExistsException("The username %1$s is already exists".formatted(newUserDto.username()));
        return save(newUserDto);


    }

    private UserDto save(NewUserDto newUserDto) {
        return Optional.of(newUserDto)
                .map(userMapper::toUserFromNewUserDto)
                .map(userRepository::save)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityCreationException("User '%1$s' creation error".formatted(newUserDto.username())));
    }


    @Transactional
    public void delete(Long id) {
        userRepository.findUserById(id)
                .ifPresentOrElse(user -> {
                    userRepository.delete(user);
                    userRepository.flush();
                }, () -> {
                    throw new EntityNotFoundException("The user '%1$s' is not found".formatted(id));
                });

    }



}
