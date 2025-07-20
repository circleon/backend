package com.circleon.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessResponse {

    private String message;

    @Builder
    SuccessResponse(String message) {
        this.message = message;
    }
}
