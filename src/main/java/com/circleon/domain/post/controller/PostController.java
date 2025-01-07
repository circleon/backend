package com.circleon.domain.post.controller;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.*;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/circles/{circleId}/posts")
    public ResponseEntity<PostCreateResponse> createPost(@LoginUser Long userId,
                                                         @PathVariable Long circleId,
                                                         @Valid @ModelAttribute PostCreateRequest postCreateRequest){
        PostCreateResponse postCreateResponse = postService.createPost(userId, circleId, postCreateRequest);

        return ResponseEntity.ok(postCreateResponse);
    }

    @GetMapping("/circles/{circleId}/posts")
    public ResponseEntity<PaginatedResponse<PostResponse>> findPagedPosts(@LoginUser Long userId,
                                                                          @PathVariable Long circleId,
                                                                          @RequestParam PostType postType,
                                                                          @RequestParam int page,
                                                                          @RequestParam int size){

        Sort sort = Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);

        PageableValidator.validatePageable(pageable, List.of("isPinned", "createdAt"), 100);

        PaginatedResponse<PostResponse> pagedPosts = postService.findPagedPosts(userId, circleId, postType, pageable);

        return ResponseEntity.ok(pagedPosts);
    }

    @PutMapping("/circles/{circleId}/posts/{postId}")
    public ResponseEntity<PostUpdateResponse> updatePost(@LoginUser Long userId,
                                                         @PathVariable Long circleId,
                                                         @PathVariable Long postId,
                                                         @Valid @RequestBody PostUpdateRequest postUpdateRequest){
        PostUpdateResponse postUpdateResponse = postService.updatePost(userId, circleId, postId, postUpdateRequest);
        return ResponseEntity.ok(postUpdateResponse);
    }

    @PutMapping("/circles/{circleId}/posts/{postId}/pin")
    public ResponseEntity<SuccessResponse> updatePin(@LoginUser Long userId,
                                                     @PathVariable Long circleId,
                                                     @PathVariable Long postId,
                                                     @Valid @RequestBody PostPinUpdateRequest postPinUpdateRequest){

        postService.updatePin(userId, circleId, postId, postPinUpdateRequest);
        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @DeleteMapping("/circles/{circleId}/posts/{postId}")
    public ResponseEntity<SuccessResponse> deletePost(@LoginUser Long userId,
                                                      @PathVariable Long circleId,
                                                      @PathVariable Long postId){
        postService.deletePost(userId, circleId, postId);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @GetMapping("/posts/images/{circleId}/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable Long circleId,
                                              @PathVariable String directory,
                                              @PathVariable String filename,
                                              @RequestHeader("Content-Type") String contentTypeHeader){

        MediaType mediaType;

        try {
            mediaType = MediaType.parseMediaType(contentTypeHeader);
        }catch (InvalidMediaTypeException e){
            throw new CommonException(CommonResponseStatus.BAD_REQUEST, e.getMessage());
        }
        String filePath = circleId + "/" + directory + "/" + filename + "." + mediaType.getSubtype();

        Resource resource = postService.loadImageAsResource(filePath);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
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

    @ExceptionHandler(CircleException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(CircleException e) {

        CircleResponseStatus status = e.getStatus();

        log.warn("CircleException: {}", e.getMessage());

        log.warn("CircleException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }
}
