package com.circleon.domain.post.controller;

import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.dto.*;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/circles/{circleId}/posts/{postId}/comments")
    public ResponseEntity<CommentCreateResponse> createComment(@LoginUser Long userId,
                                                               @PathVariable Long circleId,
                                                               @PathVariable Long postId,
                                                               @Valid @RequestBody CommentCreateRequest commentCreateRequest){
        CommentCreateResponse commentCreateResponse = commentService.createComment(RequestIdentifiers.of(userId, circleId, postId), commentCreateRequest);
        return ResponseEntity.ok(commentCreateResponse);
    }

    @GetMapping("/circles/{circleId}/posts/{postId}/comments")
    public ResponseEntity<PaginatedResponse<CommentSearchResponse>> findPagedComments(@LoginUser Long userId,
                                                                                      @PathVariable Long circleId,
                                                                                      @PathVariable Long postId,
                                                                                      @RequestParam int page,
                                                                                      @RequestParam int size){

        Sort sort = Sort.by(Sort.Order.asc("createdAt"));

        Pageable pageable = PageRequest.of(page, size, sort);

        PageableValidator.validatePageable(pageable, List.of("createdAt"), 100);

        PaginatedResponse<CommentSearchResponse> pagedComments = commentService.findPagedComments(RequestIdentifiers.of(userId, circleId, postId), pageable);

        return ResponseEntity.ok(pagedComments);
    }

    @PutMapping("/circles/{circleId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentUpdateResponse> updateComment(@LoginUser Long userId,
                                                               @PathVariable Long circleId,
                                                               @PathVariable Long postId,
                                                               @PathVariable Long commentId,
                                                               @Valid @RequestBody CommentUpdateRequest commentUpdateRequest){
        CommentUpdateResponse commentUpdateResponse = commentService
                .updateComment(
                        RequestIdentifiers.of(userId, circleId, postId),
                        commentId,
                        commentUpdateRequest
                );

        return ResponseEntity.ok(commentUpdateResponse);
    }

    @DeleteMapping("/circles/{circleId}/posts/{postId}/comments/{commentId}")
    public ResponseEntity<SuccessResponse> deleteComment(@LoginUser Long userId,
                                                         @PathVariable Long circleId,
                                                         @PathVariable Long postId,
                                                         @PathVariable Long commentId){
        commentService.deleteComment(RequestIdentifiers.of(userId, circleId, postId), commentId);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

}
