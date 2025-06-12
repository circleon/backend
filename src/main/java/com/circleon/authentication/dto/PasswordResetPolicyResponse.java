package com.circleon.authentication.dto;

public record PasswordResetPolicyResponse(Long policyId) {
    public static PasswordResetPolicyResponse of(Long policyId) {
        return new PasswordResetPolicyResponse(policyId);
    }
}
