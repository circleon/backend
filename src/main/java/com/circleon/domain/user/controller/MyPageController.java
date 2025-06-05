package com.circleon.domain.user.controller;

import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;

import com.circleon.common.dto.PaginatedResponse;

import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.user.dto.*;
import com.circleon.domain.user.service.MyPageService;
import com.circleon.domain.user.service.UserFileStore;
import com.circleon.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/me")
public class MyPageController {

    public static final int CACHE_MAX_AGE = 365;
    private final MyPageService myPageService;
    private final UserService userService;
    private final UserFileStore userFileStore;

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

    @GetMapping
    public ResponseEntity<UserResponse> findMe(@LoginUser Long loginId) {
        return ResponseEntity.ok(UserResponse.from(myPageService.findMeById(loginId)));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateMe(@LoginUser Long userId, @RequestBody UserUpdate userUpdate) {
        return ResponseEntity.ok(UserResponse.from(myPageService.updateMe(userId, userUpdate.getUsername())));
    }

    @PutMapping("/image")
    public ResponseEntity<SuccessResponse> updateImage(@LoginUser Long userId, @Valid @ModelAttribute UserImageUpdate userImageUpdate) {
        myPageService.updateImage(userId, userImageUpdate.getImage());
        return ResponseEntity.ok(SuccessResponse.builder().message("success").build());
    }

    @DeleteMapping("/image")
    public ResponseEntity<SuccessResponse> deleteImage(@LoginUser Long userId) {
        myPageService.deleteImage(userId);
        return ResponseEntity.ok(SuccessResponse.builder().message("success").build());
    }

    @GetMapping("/me/image/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable String directory,
                                              @PathVariable String filename){
        String filePath = directory + "/" + filename;
        Resource resource = userService.loadImageAsResource(filePath);
        String extension = userFileStore.extractExtension(filename);
        MediaType mediaType = extension.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;;


        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl
                        .maxAge(CACHE_MAX_AGE, TimeUnit.DAYS)
                        .cachePublic()
                        .immutable()
                )
                .body(resource);
    }

}
