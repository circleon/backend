package com.circleon.domain.post.exception;

import com.circleon.domain.post.PostResponseStatus;
import lombok.Getter;

@Getter
public class PostException extends RuntimeException {

    PostResponseStatus status;

    public PostException(PostResponseStatus status, String message) {
        super(message);
        this.status = status;
    }
}
