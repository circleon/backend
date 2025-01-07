package com.circleon.authentication.email.service;

import com.circleon.authentication.email.dto.AwsSesEmailRequest;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    void sendEmail(AwsSesEmailRequest awsSesEmailRequest);

    CompletableFuture<Void> sendAsyncEmail(AwsSesEmailRequest awsSesEmailRequest);

    String generateVerificationCode();
}
