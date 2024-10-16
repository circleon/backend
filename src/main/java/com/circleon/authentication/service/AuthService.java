package com.circleon.authentication.service;

import com.circleon.authentication.dto.*;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;

public interface AuthService {

    void registerUser(SignUpRequest signUpRequest);

    void sendVerificationEmail(EmailVerificationRequest emailVerificationRequest);

    void verifyVerificationCode(VerificationCodeRequest verificationCodeRequest);

    LoginResponse login(LoginRequest loginRequest);

    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logout(LogoutRequest logoutRequest);
}
