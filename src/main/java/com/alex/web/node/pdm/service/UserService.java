package com.alex.web.node.pdm.service;

import com.alex.web.node.pdm.dto.user.NewUserDto;
import com.alex.web.node.pdm.dto.user.UpdateUserDto;
import com.alex.web.node.pdm.dto.user.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Optional;

/**
 * This class is a service layer for {@link User user}.
 */

public interface UserService extends UserDetailsService,
        OAuth2UserService<OidcUserRequest, OidcUser> {

    /**
     * Gets name of current logged user from SecurityContext.
     *
     * @return name of user wrapped in optional.
     */

    Optional<String> getCurrentName();

    /**
     * Returns output-dto by id of user if finding process successful.
     *
     * @param id id of user.
     * @return output-dto.
     */

    UserDto findById(Long id);

    /**
     * Returns list of output-dto if search successful.
     *
     * @return list of output dto.
     */

    List<UserDto> findAll();

    /**
     * Updates a specific {@link User user} by input-dto and id.
     *
     * @param id            id of user.
     * @param updateUserDto input-dto.
     * @return output-dto.
     */

    UserDto update(Long id, UpdateUserDto updateUserDto);

    /**
     * Creates a new {@link User user} by input-dto.
     *
     * @param newUserDto input-dto.
     * @return output-dto.
     */

    UserDto create(NewUserDto newUserDto);

    /**
     * Removes a specific{@link User user} by id of user.
     *
     * @param id if of user.
     */

    void delete(Long id);

}
