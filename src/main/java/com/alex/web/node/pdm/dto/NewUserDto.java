package com.alex.web.node.pdm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@FieldNameConstants
public record NewUserDto(

        @NotBlank(message = "The firstname should be not blank")
        @Size(min = 2, max = 64,message = "Size of firstname should be between 0 and 255 characters ")
        String firstname,

        @NotBlank(message = "The lastname should be not blank")
        @Size(min = 2, max = 64,message = "Size of lastname should be between 0 and 255 characters ")
        String lastname,

        @NotBlank(message = "The username should be not blank")
        @Email(message = "Username should be like an email")
        String username,

        @NotBlank(message = "The password should be not blank")
        @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,12}",
                message = "The password should contains digits and big letters")
        String password,

        @NotNull(message = "The date should be not null")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                /*@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)*/
        LocalDate birthday
) {
}
