package com.circleon.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(
        @NotBlank
        String publicId,
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*_\\-+=`|\\\\(){}\\[\\]:;\"'<>,.?/])(?=.*[a-zA-Z]).{8,12}$",
                message = "패스워드는 숫자, 영문자, 특수문자로 8~12글자입니다."
        )
        String newPassword
) {
}
