package com.circleon.domain.user.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.domain.user.dto.UserResponse;
import com.circleon.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> findMe(@LoginUser Long loginId) {
        UserResponse userResponse = userService.findMeById(loginId);
        return ResponseEntity.ok(userResponse);
    }
}
