package com.circleon.domain.post.controller;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.*;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.service.PostService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private final FileStore postFileStore;

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

    @GetMapping("/circles/{circleId}/posts/{postId}/img-url")
    public ResponseEntity<PostImageResponse> findPostImage(@LoginUser Long userId,
                                                           @PathVariable Long circleId,
                                                           @PathVariable Long postId){
        return ResponseEntity.ok(postService.findPost(userId, circleId, postId));
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
        postService.softDeletePost(userId, circleId, postId);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @GetMapping("/posts/images/{circleId}/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable Long circleId,
                                              @PathVariable String directory,
                                              @PathVariable String filename,
                                              @RequestParam String expires,
                                              @RequestParam String signature){


        String filePath = circleId + "/" + directory + "/" + filename;
        Resource resource = postService.loadImageAsResource(filePath, expires, signature);
        String extension = postFileStore.extractExtension(filename);
        MediaType mediaType = extension.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;;

        //캐시 만료 시간 계산
        long now = Instant.now().getEpochSecond();
        long exp = Long.parseLong(expires);
        long maxAge = Math.max(0, exp - now);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(maxAge, TimeUnit.SECONDS).cachePublic())
                .body(resource);
    }

}
