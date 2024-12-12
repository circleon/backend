package com.circleon.domain.schedule.exception;

import com.circleon.domain.schedule.ScheduleResponseStatus;
import lombok.Getter;


@Getter
public class ScheduleException extends RuntimeException {

    ScheduleResponseStatus status;

    public ScheduleException(ScheduleResponseStatus status, String message) {
        super(message);
        this.status = status;
    }
}
