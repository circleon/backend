package com.circleon.domain.admin.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.admin.AdminResponseStatus;
import com.circleon.domain.admin.dto.LoginRequest;
import com.circleon.domain.admin.dto.LoginResponse;
import com.circleon.domain.admin.dto.Token;
import com.circleon.domain.admin.dto.UserInfo;
import com.circleon.domain.admin.exception.AdminException;
import com.circleon.domain.admin.service.AdminAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {


    private final AdminAuthService adminAuthService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        LoginResponse loginResponse = adminAuthService.login(loginRequest, response);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<Token> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                         HttpServletResponse response){

        if(refreshToken == null){
            throw new AdminException(AdminResponseStatus.REFRESH_TOKEN_NOT_FOUND, "[Admin] Refresh token not found");
        }

        Token newAccessToken = adminAuthService.refresh(refreshToken, response);
        return ResponseEntity.ok(newAccessToken);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getAdminInfo(@LoginUser Long userId){
        UserInfo adminInfo = adminAuthService.getAdminInfo(userId);
        return ResponseEntity.ok(adminInfo);
    }
}
