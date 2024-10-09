package com.circleon.authentication.controller;


import com.circleon.authentication.dto.SignUpRequest;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;
import com.circleon.authentication.service.AuthService;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.exception.UserException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(SuccessResponse.builder().message("signup success").build());
    }

    @PostMapping("/verification")
    public ResponseEntity<SuccessResponse> sendVerificationEmail(@Valid @RequestBody EmailVerificationRequest emailVerificationRequest) {
        authService.sendVerificationEmail(emailVerificationRequest);
        return ResponseEntity.ok(SuccessResponse.builder().message("Verification email sent").build());
    }

    @PostMapping("/verification-code")
    public ResponseEntity<SuccessResponse> verifyCode(@Valid @RequestBody VerificationCodeRequest verificationCodeRequest) {
        authService.verifyVerificationCode(verificationCodeRequest);
        return ResponseEntity.ok(SuccessResponse.builder().message("인증 성공").build());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {

        UserResponseStatus status = e.getStatus();

        log.warn("UserException: {} {} {}", status.getHttpStatus(), status.getCode(), status.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();
        return ResponseEntity.status(status.getHttpStatus()).body(errorResponse);
    }
}
