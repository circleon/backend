package com.circleon.authentication.controller;


import com.circleon.authentication.dto.*;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;
import com.circleon.authentication.email.service.AsyncService;
import com.circleon.authentication.service.AuthService;
import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.exception.UserException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AsyncService asyncService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(SuccessResponse.builder().message("signup success").build());
    }

    @PostMapping("/verification")
    public ResponseEntity<SuccessResponse> sendVerificationEmail(@Valid @RequestBody EmailVerificationRequest emailVerificationRequest) {
        log.info("1");
        authService.sendVerificationEmail(emailVerificationRequest);
        log.info("1");
        return ResponseEntity.ok(SuccessResponse.builder().message("Verification email sent").build());
    }

    @PostMapping("/verification-code")
    public ResponseEntity<SuccessResponse> verifyCode(@Valid @RequestBody VerificationCodeRequest verificationCodeRequest) {
        authService.verifyVerificationCode(verificationCodeRequest);
        return ResponseEntity.ok(SuccessResponse.builder().message("인증 성공").build());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshAccessToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        RefreshTokenResponse refreshTokenResponse;

        try{
            refreshTokenResponse = authService.refreshToken(refreshTokenRequest);
        }catch (OptimisticLockingFailureException e){
            throw new CommonException(CommonResponseStatus.ACCESS_TOKEN_ALREADY_REFRESH);
        }

        if(refreshTokenResponse == null) {
            throw new CommonException(CommonResponseStatus.REFRESH_TOKEN_INVALID);
        }

        return ResponseEntity.ok(refreshTokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest);
        return ResponseEntity.ok(SuccessResponse.builder().message("로그아웃 성공").build());
    }


    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {

        UserResponseStatus status = e.getStatus();

        log.warn("UserException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();
        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @PostMapping("/test")
    public CompletableFuture<ResponseEntity<SuccessResponse>> test(@RequestBody Map<String, String> emailMap) {

        log.info("톰켓 스레드 시작 : {}", Thread.currentThread().getName());
        CompletableFuture<Void> future = authService.sendAsyncVerificationEmail(emailMap.get("email"));
        log.info("톰켓 스레드 종료 : {}", Thread.currentThread().getName());

        return future.thenApply(e->ResponseEntity.ok(SuccessResponse.builder().message("Success").build()));
    }
}
