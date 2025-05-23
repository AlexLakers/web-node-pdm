package com.alex.web.node.pdm.service.impl;

import com.alex.web.node.pdm.config.security.CustomUserDetails;
import com.alex.web.node.pdm.config.security.CustomOidcUser;
import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import com.alex.web.node.pdm.exception.EntityCreationException;
import com.alex.web.node.pdm.exception.EntityNotFoundException;
import com.alex.web.node.pdm.exception.UsernameAlreadyExistsException;
import com.alex.web.node.pdm.mapper.user.UserMapper;
import com.alex.web.node.pdm.model.Role;
import com.alex.web.node.pdm.model.User;
import com.alex.web.node.pdm.model.enums.Provider;
import com.alex.web.node.pdm.model.enums.RoleName;
import com.alex.web.node.pdm.repository.UserRepository;
import com.alex.web.node.pdm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("The method 'loadUserByUsername' with arg:{}", username);
        UserDetails userDetails = getCustomUserDetails(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        log.debug("The UserDetails User: {} has been loaded", userDetails);
        return userDetails;

    }

    private Optional<CustomUserDetails> getCustomUserDetails(String username) {
        log.debug("The method 'getCustomUserDetails' arg:{}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toCustomUserDetails);
    }

    public Optional<String> getCurrentName() {
        log.debug("The method 'getCurrentName'");
        var s = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(user -> ((UserDetails) user).getUsername());
    }

    public UserDto findById(Long id) {
        log.debug("The method 'findById' with arg:{}", id);
        UserDto foundUserDto = userRepository.findUserById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("The user '%1$s' is not found".formatted(id)));
        log.info("The user with id :{} has been found", id);
        return foundUserDto;
    }

    // @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> findAll() {
        log.debug("The method 'findAll' without args");
        List<UserDto> dtoList = userMapper.toUserDtoList(userRepository.findAll());
        log.info("List of users has been generated:{}", dtoList);
        return dtoList;
    }

    @Transactional
    public UserDto update(Long id, UpdateUserDto updateUserDto) {
        log.debug("The method 'update' with arg:{}", updateUserDto);
        UserDto updatedUserDto = userRepository.findUserById(id)
                .map(user -> {
                    userMapper.updateUser(user, updateUserDto);
                    return userRepository.saveAndFlush(user);
                })
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("The user '%1$s' is not found".formatted(id)));
        log.info("The user with id :{} has been updated", id);
        return updatedUserDto;

    }

    @Transactional
    public UserDto create(NewUserDto newUserDto) {
        log.debug("The method 'create' with arg:{}", newUserDto);
        if (userRepository.existsUserByUsername(newUserDto.username()))
            throw new UsernameAlreadyExistsException("The username %1$s is already exists".formatted(newUserDto.username()));
        UserDto savedUserDto = save(newUserDto);
        log.info("A new user with id:{} has been created", savedUserDto.id());
        return savedUserDto;


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
        log.debug("The method 'delete' with arg:{}", id);
        userRepository.findUserById(id)
                .ifPresentOrElse(user -> {
                    userRepository.delete(user);
                    userRepository.flush();
                }, () -> {
                    throw new EntityNotFoundException("The user '%1$s' is not found".formatted(id));
                });
        log.info("The user with id:{} has been deleted", id);
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("The method 'loadUser' with arg:{}", userRequest);
        String email = userRequest.getIdToken().getClaim("email");
        String name = userRequest.getIdToken().getClaim("name");
        CustomOidcUser oidcUser = getCustomUserDetails(email)
                .map(userDetails -> new CustomOidcUser(new DefaultOidcUser(userDetails.getAuthorities(), userRequest.getIdToken()), userDetails.getId()))
                .orElseGet(() -> {
                    User savedUser = saveOidcUserWithDefaultRole(email, name);
                    return new CustomOidcUser(new DefaultOidcUser(savedUser.getRoles(), userRequest.getIdToken()), savedUser.getId());
                });
        log.debug("The Oidc user :{} has been loaded and generated", oidcUser);
        return oidcUser;
    }


    private User saveOidcUserWithDefaultRole(String email, String name) {
        User user = User.builder().username(email).firstname(name)
                .provider(Provider.OAUTH2_GOOGLE)
                .roles(Collections.singletonList(Role.builder().roleName(RoleName.USER).build()))
                .build();
        return Optional.ofNullable(userRepository.save(user))
                .orElseThrow(() -> new EntityCreationException("User '%1$s' creation error".formatted(email)));
    }

}

