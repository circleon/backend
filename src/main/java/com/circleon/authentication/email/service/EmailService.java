package com.circleon.authentication.email.service;

import com.circleon.authentication.email.dto.AwsSesEmailRequest;

public interface EmailService {

    void sendEmail(AwsSesEmailRequest awsSesEmailRequest);

    String generateVerificationCode();
}
