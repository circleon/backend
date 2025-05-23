package com.circleon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Reporter {

    private Long userId;

    private String userName;

    public static Reporter of(Long userId, String userName) {
        return Reporter.builder()
                .userId(userId)
                .userName(userName)
                .build();
    }
}
