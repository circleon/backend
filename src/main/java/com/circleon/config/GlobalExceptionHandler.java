package com.circleon.config;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.user.UserResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {

        log.warn("Max upload size exceeded", e);

        CommonResponseStatus fileSizeExceeded = CommonResponseStatus.FILE_SIZE_EXCEEDED;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(fileSizeExceeded.getCode())
                .errorMessage(fileSizeExceeded.getMessage())
                .build();

        return ResponseEntity.status(fileSizeExceeded.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException e) {

        CommonResponseStatus status =  e.getStatus();

        log.warn("CommonException: {} {}", status.getHttpStatusCode(), status.getCode());

        log.warn("CommonException: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorMessage(e.getMessage())
                .errorCode(status.getCode())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.warn("MethodArgumentNotValidException: {}", ex.getMessage());

        String errorMessage = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse;

        if (isUnivEmailErrorAndNotEmailError(ex)) {

            UserResponseStatus invalidUnivEmailDomain = UserResponseStatus.INVALID_UNIV_EMAIL_DOMAIN;
            errorResponse = ErrorResponse.builder()
                    .errorCode(invalidUnivEmailDomain.getCode())
                    .errorMessage(invalidUnivEmailDomain.getMessage())
                    .build();
        }else{
            errorResponse = ErrorResponse.builder()
                    .errorCode(CommonResponseStatus.BAD_REQUEST.getCode())
                    .errorMessage(errorMessage)
                    .build();
        }

        return ResponseEntity.badRequest().body(errorResponse);
    }

    private boolean isUnivEmailErrorAndNotEmailError(MethodArgumentNotValidException ex) {
        boolean isUnivEmailError = ex.getFieldErrors().stream()
                .anyMatch(error -> error.getCode() != null && error.getCode().equals("UnivEmail"));

        boolean isEmailError = ex.getFieldErrors().stream()
                .anyMatch(error -> error.getCode() != null && error.getCode().equals("Email"));

        return isUnivEmailError && !isEmailError;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {

        log.error("RuntimeException message: {}", e.getMessage());
        log.error("RuntimeException", e);

        CommonResponseStatus internalServerError = CommonResponseStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(internalServerError.getCode())
                .errorMessage(internalServerError.getMessage())
                .build();
        return ResponseEntity.status(internalServerError.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Exception", e);

        CommonResponseStatus internalServerError = CommonResponseStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(internalServerError.getCode())
                .errorMessage(internalServerError.getMessage())
                .build();
        return ResponseEntity.status(internalServerError.getHttpStatusCode()).body(errorResponse);
    }
}
