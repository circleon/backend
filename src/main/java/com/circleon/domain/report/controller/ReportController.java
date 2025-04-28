package com.circleon.domain.report.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.report.ReportResponseStatus;
import com.circleon.domain.report.ReportType;
import com.circleon.domain.report.dto.CreateReportCommand;
import com.circleon.domain.report.dto.ReportCreateRequest;
import com.circleon.domain.report.exception.ReportException;
import com.circleon.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/circles")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/{circleId}/reports")
    public ResponseEntity<SuccessResponse> createCircleReport(@LoginUser Long userId,
                                                            @PathVariable Long circleId,
                                                            @RequestBody ReportCreateRequest reportCreateRequest) {

        CreateReportCommand createReportCommand = CreateReportCommand.of(ReportType.CIRCLE, circleId, userId, circleId, reportCreateRequest.getReason());
        reportService.createReport(createReportCommand);
        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @PostMapping("/{circleId}/posts/{postId}/reports")
    public ResponseEntity<SuccessResponse> createPostReport(@LoginUser Long userId,
                                                            @PathVariable Long circleId,
                                                            @PathVariable Long postId,
                                                            @RequestBody ReportCreateRequest reportCreateRequest) {

        CreateReportCommand createReportCommand = CreateReportCommand.of(ReportType.POST, postId, userId, circleId, reportCreateRequest.getReason());
        reportService.createReport(createReportCommand);
        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @PostMapping("/{circleId}/comments/{commentId}/reports")
    public ResponseEntity<SuccessResponse> createCommentReport(@LoginUser Long userId,
                                                               @PathVariable Long circleId,
                                                               @PathVariable Long commentId,
                                                               @RequestBody ReportCreateRequest reportCreateRequest) {

        CreateReportCommand createReportCommand = CreateReportCommand.of(ReportType.COMMENT, commentId, userId, circleId, reportCreateRequest.getReason());
        reportService.createReport(createReportCommand);
        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }


    @ExceptionHandler(ReportException.class)
    public ResponseEntity<ErrorResponse> handleReportException(ReportException e) {

        ReportResponseStatus status = e.getStatus();

        log.error("ReportException: {}", e.getMessage());

        log.error("ReportException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());


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


        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }
}
