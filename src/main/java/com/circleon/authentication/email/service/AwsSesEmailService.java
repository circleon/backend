package com.circleon.authentication.email.service;

import com.circleon.authentication.email.dto.AwsSesEmailRequest;

import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsSesEmailService implements EmailService{

    private final SesClient sesClient;

    @Override
    public void sendEmail(AwsSesEmailRequest awsSesEmailRequest) {

        SendEmailRequest emailRequest = awsSesEmailRequest.toSendEmailRequest();

        try{
            sesClient.sendEmail(emailRequest);
            log.info("Email sent");
        }catch(Exception e){
            log.error("메시지 발생 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Async("emailTaskExecutor")
    @Override
    public CompletableFuture<Void> sendAsyncEmail(AwsSesEmailRequest awsSesEmailRequest) {

        SendEmailRequest emailRequest = awsSesEmailRequest.toSendEmailRequest();

        try{
            sesClient.sendEmail(emailRequest);
        }catch(Exception e){
            throw new UserException(UserResponseStatus.EMAIL_SERVICE_UNAVAILABLE, "[sendAsyncEmail] 이메일 전송 서비스 이용불가", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }


}
