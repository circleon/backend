package com.circleon.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;

    @Builder
    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
