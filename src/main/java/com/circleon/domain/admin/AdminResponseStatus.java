package com.circleon.domain.admin;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AdminResponseStatus {


    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "1000", "존재하지 않는 관리자"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "1001", "리프레쉬 토큰이 존재하지 않음"),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "1002", "신고가 존재하지 않습니다."),
    REPORT_TYPE_INVALID(HttpStatus.BAD_REQUEST.value(), "1003", "신고 타입이 맞지 않습니다.");
    private final int httpStatusCode;
    private final String code;
    private final String message;

    AdminResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
