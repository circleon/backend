package com.circleon.domain.schedule;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ScheduleResponseStatus {

    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "071", "일정이 존재하지 않습니다.");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    ScheduleResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

}
