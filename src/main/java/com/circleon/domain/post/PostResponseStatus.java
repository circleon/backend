package com.circleon.domain.post;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PostResponseStatus {

    POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "061", "게시글이 존재하지 않습니다."),
    NOT_NOTICE(HttpStatus.BAD_REQUEST.value(), "062", "공지사항이 아닙니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "063", "댓글이 존재하지 않습니다.");

    private final int httpStatusCode;
    private final String code;
    private final String message;

    PostResponseStatus(int httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }
}
