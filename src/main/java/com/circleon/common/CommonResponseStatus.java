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
    ACCESS_TOKEN_ALREADY_REFRESH(HttpStatus.UNAUTHORIZED.value(), "003", "Access 토큰 이미 재발급"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN.value(), "004", "권한이 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED.value(), "005", "로그인이 필요합니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "006", "존재하지 않은 유저입니다."),

    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST.value(), "007", "파일 용량 초과"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "5", "서버 에러");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    CommonResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
