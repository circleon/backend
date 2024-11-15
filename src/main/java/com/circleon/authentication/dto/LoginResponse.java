package com.circleon.authentication.dto;

import com.circleon.domain.user.entity.UnivCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private UserDto user;

    private TokenDto token;

    public static LoginResponse of(UserDto user, TokenDto token) {
        return LoginResponse.builder()
                .user(user)
                .token(token)
                .build();
    }
}
