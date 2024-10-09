package com.circleon.config;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(CommonResponseStatus.INTERNAL_SERVER_ERROR.getCode())
                .errorMessage(CommonResponseStatus.INTERNAL_SERVER_ERROR.getMessage())
                .build();
        return ResponseEntity.status(CommonResponseStatus.INTERNAL_SERVER_ERROR.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Exception", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(CommonResponseStatus.INTERNAL_SERVER_ERROR.getCode())
                .errorMessage(CommonResponseStatus.INTERNAL_SERVER_ERROR.getMessage())
                .build();
        return ResponseEntity.status(CommonResponseStatus.INTERNAL_SERVER_ERROR.getHttpStatus()).body(errorResponse);
    }
}
