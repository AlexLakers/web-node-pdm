package com.alex.web.node.pdm.dto.user;

import com.alex.web.node.pdm.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
/**
 * This is input dto-object which contains all the necessary fields from {@link User user}.
 * It is used for creation process a new user.
 *
 * @param firstname firstname field from model.
 * @param lastname  lastname field from model.
 * @param username  username field from model.
 * @param password  password field from model.
 * @param birthday  birthday field from model.
 */

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
        @PastOrPresent
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                /*@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)*/
        LocalDate birthday
) {
}
