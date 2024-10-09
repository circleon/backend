package com.circleon.authentication.service;

import com.circleon.authentication.dto.SignUpRequest;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;

public interface AuthService {

    public void registerUser(SignUpRequest signUpRequest);

    public void sendVerificationEmail(EmailVerificationRequest emailVerificationRequest);

    public void verifyVerificationCode(VerificationCodeRequest verificationCodeRequest);
}
