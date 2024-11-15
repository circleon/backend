package com.circleon.domain.post;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PostResponseStatus {

    //임시
    EXAMPLE(HttpStatus.NOT_FOUND.value(), "000", "일단예시");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    PostResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
