package com.circleon.domain.report;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReportResponseStatus {

    REPORT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "081", "신고 가능 대상이 아닙니다."),
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "042", "가입하지 않은 동아리입니다.");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    ReportResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
