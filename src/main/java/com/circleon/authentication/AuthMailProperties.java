package com.circleon.authentication;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AuthMailProperties {

    private final String sourceMail;

    public AuthMailProperties(@Value("${auth.source-mail}") final String sourceMail) {
        this.sourceMail = sourceMail;
    }
}
