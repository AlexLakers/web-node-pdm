package com.alex.web.node.pdm.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Sort;
//This class is a validator handler for ordering
public class OrderDirectionValidator implements ConstraintValidator<ValidOrderDirection, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null||s.isEmpty()) return true;
        try {
            Sort.Direction.fromString(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

    }
}
