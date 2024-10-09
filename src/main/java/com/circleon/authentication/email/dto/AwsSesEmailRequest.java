package com.circleon.authentication.email.dto;

import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.ses.model.*;

@Getter
public class AwsSesEmailRequest {

    private String sender;
    private String recipient;
    private String subject;
    private String content;

    @Builder
    public AwsSesEmailRequest(String sender, String recipient, String subject, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }

    public SendEmailRequest toSendEmailRequest() {

        Destination destination = Destination.builder()
                .toAddresses(this.recipient)
                .build();

        Content content = createContent(this.content);

        Content sub = createContent(this.subject);

        Body body = Body.builder()
                .html(content)
                .build();

        Message msg = Message.builder()
                .subject(sub)
                .body(body)
                .build();

        return SendEmailRequest.builder()
                .destination(destination)
                .message(msg)
                .source(this.sender)
                .build();
    }

    private Content createContent(String text) {
        return Content.builder()
                .charset("UTF-8")
                .data(text)
                .build();
    }

}
