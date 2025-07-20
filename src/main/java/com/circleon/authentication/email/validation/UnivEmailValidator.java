package com.circleon.authentication.email.validation;

import com.circleon.domain.user.entity.UnivCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UnivEmailValidator implements ConstraintValidator<UnivEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if(email == null || email.isEmpty()) {
            return false;
        }

        for(UnivCode univCode : UnivCode.values()) {
            if(email.endsWith("@" + univCode.getEmail())) {
                return true;
            }
        }

        return false;
    }
}
