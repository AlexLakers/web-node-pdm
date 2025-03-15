package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService,
        OAuth2UserService<OidcUserRequest, OidcUser> {

    Optional<String> getCurrentName();

    UserDto findById(Long id);

    List<UserDto> findAll();

    UserDto update(Long id, UpdateUserDto updateUserDto);

    UserDto create(NewUserDto newUserDto);

    void delete(Long id);

}
