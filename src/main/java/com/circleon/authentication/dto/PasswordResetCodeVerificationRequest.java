package com.circleon.authentication.dto;

import com.circleon.authentication.AuthConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordResetCodeVerificationRequest(
        @NotNull(message = "숫자를 입력해야 합니다.")
        Long policyId,

        @NotBlank(message = "code를 입력해야합니다.")
        @Size(min = AuthConstants.VERIFICATION_CODE_LENGTH,
                max = AuthConstants.VERIFICATION_CODE_LENGTH,
                message = "인증 코드는 정확히 {max}글자여야 합니다.")
        String code
) {
}
