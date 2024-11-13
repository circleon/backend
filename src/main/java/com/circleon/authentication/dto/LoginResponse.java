package com.circleon.authentication.dto;

import com.circleon.domain.user.entity.UnivCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long userId;

    private String username;

    private UnivCode univCode;

    private String accessToken;

    private String refreshToken;
}
