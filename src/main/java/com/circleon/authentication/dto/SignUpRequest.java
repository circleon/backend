package com.circleon.authentication.dto;

import com.circleon.authentication.email.validation.UnivEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @UnivEmail(message = "허용된 대학 이메일 도메인이 아닙니다.")
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*_\\-+=`|\\\\(){}\\[\\]:;\"'<>,.?/])(?=.*[a-zA-Z]).{8,12}$",
            message = "패스워드는 숫자, 영문자, 특수문자로 8~12글자입니다."
    )
    private String password;

    @NotBlank
    private String username;

    @Builder
    public SignUpRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
