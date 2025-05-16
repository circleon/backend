package com.circleon.domain.user.controller;

import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;

import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.dto.CommentedPostResponse;
import com.circleon.domain.user.dto.MyPostResponse;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/me")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/posts")
    public ResponseEntity<PaginatedResponse<MyPostResponse>> findMyPosts(@LoginUser Long userId,
                                                                         Pageable pageable) {

        PageableValidator.validatePageable(pageable, List.of("createdAt"), 100);

        PaginatedResponse<MyPostResponse> myPosts = myPageService.findMyPosts(userId, pageable);

        return ResponseEntity.ok(myPosts);
    }

    @GetMapping("/commented-posts")
    public ResponseEntity<PaginatedResponse<CommentedPostResponse>> findMyCommentedPosts(@LoginUser Long userId,
                                                                                         Pageable pageable) {
        PageableValidator.validatePageable(pageable, List.of("createdAt"), 100);

        PaginatedResponse<CommentedPostResponse> myCommentedPosts = myPageService.findMyCommentedPosts(userId, pageable);

        return ResponseEntity.ok(myCommentedPosts);
    }
}
