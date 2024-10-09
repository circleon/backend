package com.circleon.authentication.email.dto;

import com.circleon.authentication.email.validation.UnivEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationRequest {

    @NotBlank
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @UnivEmail(message = "허용된 대학 이메일 도메인이 아닙니다.")
    private String email;

    @Builder
    public EmailVerificationRequest(String email) {
        this.email = email;
    }

}
