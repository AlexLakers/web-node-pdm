package com.alex.web.node.pdm.dto.user;

import com.alex.web.node.pdm.model.User;
import jakarta.validation.constraints.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
/**
 * This is input dto-object which contains all the necessary fields from {@link User user}.
 * It is used for updating a specific user.
 *
 * @param firstname firstname of user.
 * @param lastname  lastname of user.
 * @param username  username of user.
 * @param birthday  birthday of user.
 */

@FieldNameConstants
public record UpdateUserDto (
        @NotBlank(message = "The firstname should be not blank")
        @Size(min = 2, max = 64,message = "Size of firstname should be between 0 and 255 characters ")
        String firstname,
        String lastname,

        @NotBlank(message = "The username should be not blank")
        @Email(message = "Username should be like an email")
        String username,

        @PastOrPresent
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthday
){
}
