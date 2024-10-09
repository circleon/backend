package com.circleon.authentication.email.service;

import com.circleon.authentication.email.dto.AwsSesEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.UUID;

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
        }
    }

    @Override
    public String generateVerificationCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
