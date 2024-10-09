package com.circleon.authentication.email.dto;

import com.circleon.authentication.AuthConstants;
import com.circleon.authentication.email.validation.UnivEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerificationCodeRequest {

    @NotBlank
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @UnivEmail(message = "허용된 대학 이메일 도메인이 아닙니다.")
    private String email;

    @NotBlank
    @Size(min = AuthConstants.VERIFICATION_CODE_LENGTH,
            max = AuthConstants.VERIFICATION_CODE_LENGTH,
            message = "인증 코드는 정확히 6글자여야 합니다.")
    private String code;

    @Builder
    public VerificationCodeRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
