package com.circleon.domain.post.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.dto.PostCreateRequest;
import com.circleon.domain.post.dto.PostCreateResponse;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/circles/{circleId}/posts")
    public ResponseEntity<PostCreateResponse> createPost(@LoginUser Long userId,
                                                         @PathVariable Long circleId,
                                                         @ModelAttribute PostCreateRequest postCreateRequest){
        PostCreateResponse postCreateResponse = postService.createPost(userId, circleId, postCreateRequest);

        return ResponseEntity.ok(postCreateResponse);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponse> handlePostException(PostException e) {

        PostResponseStatus status = e.getStatus();

        log.warn("PostException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.warn("PostException {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }
}
