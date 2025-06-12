package com.circleon.authentication.dto;

public record PasswordResetCodeVerificationResponse(
        String publicId
) {
    public static PasswordResetCodeVerificationResponse of(String publicId) {
        return new PasswordResetCodeVerificationResponse(publicId);
    }
}
