package com.circleon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private UserInfo user;

    private Token token;

    public static LoginResponse of(UserInfo userInfo, Token token) {
        return LoginResponse.builder()
                .user(userInfo)
                .token(token)
                .build();
    }
}
