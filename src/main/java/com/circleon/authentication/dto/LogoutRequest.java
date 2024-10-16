package com.circleon.authentication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogoutRequest {

    private String refreshToken;

    @Builder
    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
