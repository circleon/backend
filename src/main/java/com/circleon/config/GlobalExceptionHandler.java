package com.circleon.config;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException e) {

        CommonResponseStatus status =  e.getStatus();

        log.warn("CommonException: {} {} {}", status.getHttpStatus(), status.getMessage(), status.getCode());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorMessage(status.getMessage())
                .errorCode(status.getCode())
                .build();

        return ResponseEntity.status(status.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", "));

        log.warn("MethodArgumentNotValidException: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(CommonResponseStatus.BAD_REQUEST.getCode())
                .errorMessage(errorMessage)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {

        log.error("RuntimeException", e);

        CommonResponseStatus internalServerError = CommonResponseStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(internalServerError.getCode())
                .errorMessage(internalServerError.getMessage())
                .build();
        return ResponseEntity.status(internalServerError.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Exception", e);

        CommonResponseStatus internalServerError = CommonResponseStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(internalServerError.getCode())
                .errorMessage(internalServerError.getMessage())
                .build();
        return ResponseEntity.status(internalServerError.getHttpStatus()).body(errorResponse);
    }
}
