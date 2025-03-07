package com.circleon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Token {

    private String accessToken;

    public static Token of(String accessToken) {
        return Token.builder()
                .accessToken(accessToken)
                .build();
    }
}
