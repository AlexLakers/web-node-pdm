package com.alex.web.node.pdm.dto.user;


import com.alex.web.node.pdm.model.User;

import java.time.LocalDate;
import java.util.List;

/**
 * This is output dto-object which contains all the necessary fields from {@link User user}.
 * It's used for reading data about specific user.
 *
 * @param id        id of user.
 * @param username  username of user.
 * @param firstname firstname of user.
 * @param lastname  lastname of user.
 * @param birthday  birthday of user.
 * @param roles     roles of current user.
 * @param provider  authentication provider.
 */

public record UserDto(
        Long id,
        String username,
        String firstname,
        String lastname,
        LocalDate birthday,
        List<String> roles,
        String provider
) {
}
