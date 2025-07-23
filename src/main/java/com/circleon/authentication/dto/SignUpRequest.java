package com.circleon.authentication.dto;

import com.circleon.authentication.email.validation.UnivEmail;
import com.circleon.domain.user.entity.UserPolicyAgreement;
import jakarta.persistence.Column;
import jakarta.validation.constraints.AssertTrue;
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

    private boolean serviceTerms;

    private boolean privacyPolicies;

    private boolean communityRules;

    @AssertTrue(message = "서비스 이용약관, 개인정보 처리방침, 커뮤니티 이용규칙에 모두 동의해야 합니다.")
    public boolean isValidUserPolicyAgreement() {
        return serviceTerms && privacyPolicies && communityRules;
    }

    public UserPolicyAgreement toUserPolicyAgreement(Long userId) {
        return UserPolicyAgreement.create(serviceTerms, privacyPolicies, communityRules, userId);
    }
}
