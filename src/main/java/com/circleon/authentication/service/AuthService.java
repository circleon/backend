package com.circleon.authentication.service;

import com.circleon.authentication.dto.*;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    void registerUser(SignUpRequest signUpRequest);

    void sendVerificationEmail(EmailVerificationRequest emailVerificationRequest);

    CompletableFuture<Void> sendAsyncVerificationEmail(String email);

    void verifyVerificationCode(VerificationCodeRequest verificationCodeRequest);

    LoginResponse login(LoginRequest loginRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logout(LogoutRequest logoutRequest);
}
