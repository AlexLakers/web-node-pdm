package com.alex.web.node.pdm.dto;

import jakarta.validation.constraints.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@FieldNameConstants
public record UpdateUserDto (
        @NotBlank(message = "The firstname should be not blank")
        @Size(min = 2, max = 64,message = "Size of firstname should be between 0 and 255 characters ")
        String firstname,

        @NotBlank(message = "The lastname should be not blank")
        @Size(min = 2, max = 64,message = "Size of lastname should be between 0 and 255 characters ")
        String lastname,

        @NotBlank(message = "The username should be not blank")
        @Email(message = "Username should be like an email")
        String username,

        @FutureOrPresent
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthday
){
}
