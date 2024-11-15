package com.circleon.domain.circle.exception;

import com.circleon.domain.circle.CircleResponseStatus;
import lombok.Getter;

@Getter
public class CircleException extends RuntimeException {

    private final CircleResponseStatus status;

    public CircleException(CircleResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public CircleException(CircleResponseStatus status, String message) {
        super(message);
        this.status = status;
    }
}
