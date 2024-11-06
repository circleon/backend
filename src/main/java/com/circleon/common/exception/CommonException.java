package com.circleon.common.exception;

import com.circleon.common.CommonResponseStatus;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final CommonResponseStatus status;

    public CommonException(CommonResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public CommonException(CommonResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

}
