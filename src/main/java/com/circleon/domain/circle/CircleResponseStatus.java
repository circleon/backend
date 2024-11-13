package com.circleon.domain.circle;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CircleResponseStatus {


    CIRCLE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "041", "존재하지 않는 동아리입니다."),
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "042", "가입하지 않은 동아리입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "043", "존재하지 않는 동아리 회원입니다."),
    APPLICANT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "044", "동아리 가입 신청자가 존재하지 않습니다.");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    CircleResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
