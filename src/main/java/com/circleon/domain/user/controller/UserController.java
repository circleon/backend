package com.circleon.domain.user.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.user.dto.UserImageUpdate;
import com.circleon.domain.user.dto.UserResponse;
import com.circleon.domain.user.dto.UserUpdate;
import com.circleon.domain.user.service.UserFileStore;
import com.circleon.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserFileStore userFileStore;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> findMe(@LoginUser Long loginId) {
        return ResponseEntity.ok(UserResponse.from(userService.findMeById(loginId)));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@LoginUser Long userId, @RequestBody UserUpdate userUpdate) {
        return ResponseEntity.ok(UserResponse.from(userService.updateMe(userId, userUpdate.getUsername())));
    }

    @PutMapping("/me/image")
    public ResponseEntity<SuccessResponse> updateImage(@LoginUser Long userId, @Valid @ModelAttribute UserImageUpdate userImageUpdate) {
        userService.updateImage(userId, userImageUpdate.getImage());
        return ResponseEntity.ok(SuccessResponse.builder().message("success").build());
    }

    @DeleteMapping("/me/image")
    public ResponseEntity<SuccessResponse> deleteImage(@LoginUser Long userId) {
        userService.deleteImage(userId);
        return ResponseEntity.ok(SuccessResponse.builder().message("success").build());
    }

    @GetMapping("/me/image/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable String directory,
                                              @PathVariable String filename,
                                              @RequestParam String expires,
                                              @RequestParam String signature){
        String filePath = directory + "/" + filename;
        Resource resource = userService.loadImageAsResource(filePath, expires, signature);
        String extension = userFileStore.extractExtension(filename);
        MediaType mediaType = extension.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;;

        long maxAge = getCacheMaxAge(expires);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(maxAge, TimeUnit.SECONDS).cachePublic())
                .body(resource);
    }

    private long getCacheMaxAge(String expires) {
        long now = Instant.now().getEpochSecond();
        long exp = Long.parseLong(expires);
        return Math.max(0, exp - now);
    }
}
