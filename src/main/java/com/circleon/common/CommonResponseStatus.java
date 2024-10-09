package com.circleon.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonResponseStatus {

    SUCCESS(HttpStatus.OK.value(), "0", "성공"),

    NO_CONTENT(HttpStatus.NO_CONTENT.value(), "0", "컨텐츠 없음"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "001", "잘못된 요청"),

    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED.value(), "002", "Access 토큰 검증 실패"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED.value(), "003", "Refresh 토큰 검증 실패"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "5", "서버 에러");

    private final int httpStatus;
    private final String code;
    private final String message;

    CommonResponseStatus(int httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
