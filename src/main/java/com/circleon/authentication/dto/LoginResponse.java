package com.circleon.authentication.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private UserInfo user;

    private TokenDto token;

    public static LoginResponse of(UserInfo user, TokenDto token) {
        return LoginResponse.builder()
                .user(user)
                .token(token)
                .build();
    }
}
