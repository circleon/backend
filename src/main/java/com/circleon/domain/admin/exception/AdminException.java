package com.circleon.domain.admin.exception;

import com.circleon.domain.admin.AdminResponseStatus;
import lombok.Getter;

@Getter
public class AdminException extends RuntimeException {

    private AdminResponseStatus status;

    public AdminException(String message) {
        super(message);
    }

    public AdminException(AdminResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AdminException(AdminResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
