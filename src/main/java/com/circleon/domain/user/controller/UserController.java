package com.circleon.domain.user.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.user.dto.UserImageUpdate;
import com.circleon.domain.user.dto.UserResponse;
import com.circleon.domain.user.dto.UserUpdate;
import com.circleon.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

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
}
