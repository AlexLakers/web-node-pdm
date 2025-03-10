package com.alex.web.node.pdm.dto;


import java.time.LocalDate;
import java.util.List;

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
