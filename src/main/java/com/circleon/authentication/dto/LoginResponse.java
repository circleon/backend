package com.circleon.authentication.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long userId;

    private String username;

    private String accessToken;

    private String refreshToken;
}
