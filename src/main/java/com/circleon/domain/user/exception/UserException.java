package com.circleon.domain.user.exception;

import com.circleon.domain.user.UserResponseStatus;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException{

    private final UserResponseStatus status;

    public UserException(UserResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public UserException(UserResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

}
