package com.alex.web.node.pdm.service.impl;

import com.alex.web.node.pdm.config.CustomUserDetails;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getCustomUserDetails(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

    }

    private Optional<CustomUserDetails> getCustomUserDetails(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toCustomUserDetails);
    }

    public Optional<String> getCurrentName() {
        var s=SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(user -> ((UserDetails) user).getUsername());
    }

    public UserDto findById(Long id) {
        return userRepository.findUserById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("The user '%1$s' is not found".formatted(id)));
    }

    // @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDto> findAll() {
        return userMapper.toUserDtoList(userRepository.findAll());
        /*userRepository.findAll().stream()
        .map(userMapper::toUserDto)
                .collect(Collectors.toList());
*/
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
                /*.map(user -> {
                            userRepository.delete(user);
                            userRepository.flush();
                            return true;
                        }
                ).orElse(false);*/
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String email = userRequest.getIdToken().getClaim("email");
        String name = userRequest.getIdToken().getClaim("name");
        return getCustomUserDetails(email)
                .map(userDetails -> new CustomOidcUser(new DefaultOidcUser(userDetails.getAuthorities(), userRequest.getIdToken()), userDetails.getId()))
                /* new DefaultOidcUser(user.getAuthorities(), userRequest.getIdToken()))*/
                .orElseGet(() -> {
                    User savedUser = saveOidcUserWithDefaultRole(email, name);
                    /*return new DefaultOidcUser(savedUser.getRoles(), userRequest.getIdToken());*/
                    return new CustomOidcUser(new DefaultOidcUser(savedUser.getRoles(), userRequest.getIdToken()), savedUser.getId());
                });


        /*user -> new CustomUserSecurity(user.getUsername(), user.getPassword(), user.getRoles(), user.getId()),*/
      /*  userRepository.save(User.builder().username(email).roles(Collections.singletonList(Role.builder().roleName(RoleName.USER).build())).build());


        return new DefaultOidcUser(userRequest.getIdToken(), );*/
    }


    private User saveOidcUserWithDefaultRole(String email, String name) {
        User user = User.builder().username(email).firstname(name)
                .provider(Provider.OAUTH2_GOOGLE)
                .roles(Collections.singletonList(Role.builder().roleName(RoleName.USER).build()))
                .build();
        return Optional.ofNullable(userRepository.save(user))
                .orElseThrow(() -> new EntityCreationException("User '%1$s' creation error".formatted(email)));
    }


       /* user==null ? new UserCreationException("User '%1$s' creation error".formatted(email))
                :user;*/

}

