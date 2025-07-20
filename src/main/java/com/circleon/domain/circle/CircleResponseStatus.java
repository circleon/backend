package com.circleon.domain.circle;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CircleResponseStatus {


    CIRCLE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "041", "존재하지 않는 동아리입니다."),
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "042", "가입하지 않은 동아리입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "043", "존재하지 않는 동아리 회원입니다."),
    ALREADY_MEMBER(HttpStatus.BAD_REQUEST.value(), "044", "이미 가입된 동아리입니다."),
    ALREADY_APPLIED(HttpStatus.BAD_REQUEST.value(), "045", "이미 가입 신청 중입니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "046", "가입 신청이 존재하지 않습니다.");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    CircleResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
