package com.circleon.config;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.admin.AdminResponseStatus;
import com.circleon.domain.admin.exception.AdminException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.report.ReportResponseStatus;
import com.circleon.domain.report.exception.ReportException;
import com.circleon.domain.schedule.ScheduleResponseStatus;
import com.circleon.domain.schedule.exception.ScheduleException;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.exception.UserException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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

    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(AdminException e) {

        AdminResponseStatus status = e.getStatus();

        log.error("AdminException: {}", e.getMessage());

        log.error("AdminException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.error("AdminException", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<ErrorResponse> handleScheduleException(ScheduleException e) {

        ScheduleResponseStatus status = e.getStatus();

        log.error("ScheduleException: {}", e.getMessage());

        log.error("ScheduleException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.error("ScheduleException", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(ReportException.class)
    public ResponseEntity<ErrorResponse> handleReportException(ReportException e) {

        ReportResponseStatus status = e.getStatus();

        log.error("ReportException: {}", e.getMessage());

        log.error("ReportException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.error("ReportException", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ErrorResponse> handlePostException(PostException e) {

        PostResponseStatus status = e.getStatus();

        log.error("PostException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.error("PostException {}", e.getMessage());

        log.error("PostException", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(CircleException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(CircleException e) {

        CircleResponseStatus status = e.getStatus();

        log.error("CircleException: {}", e.getMessage());

        log.error("CircleException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.error("CircleException", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(UserException e) {

        UserResponseStatus status = e.getStatus();

        log.error("UserException: {}", e.getMessage());

        log.error("UserException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.error("UserException", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {

        log.error("Max upload size exceeded", e);

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

        log.error("CommonException: {}", e.getMessage());

        log.error("CommonException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        log.error("CommonException", e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorMessage(status.getMessage())
                .errorCode(status.getCode())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.error("MethodArgumentNotValidException: {}", ex.getMessage());

        log.error("MethodArgumentNotValidException", ex);

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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {

        log.error("ConstraintViolationException", ex);

        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(CommonResponseStatus.BAD_REQUEST.getCode())
                .errorMessage(errorMessage)
                .build();

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
