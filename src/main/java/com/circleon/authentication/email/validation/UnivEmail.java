package com.circleon.authentication.email.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UnivEmailValidator.class)
public @interface UnivEmail {
    String message() default "허용되지 않는 이메일 도메인입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
