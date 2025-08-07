package com.circleon.domain.admin.controller;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.admin.dto.CircleReportResponse;

import com.circleon.domain.admin.dto.PostReportResponse;
import com.circleon.domain.admin.dto.ReportFindRequest;
import com.circleon.domain.admin.service.AdminReportService;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReportController {

    private final AdminReportService reportService;

    @GetMapping("/reports/circles")
    public ResponseEntity<PaginatedResponse<CircleReportResponse>> findCircleReports(ReportFindRequest request) {
        Page<CircleReportResponse> circleReports = reportService.findCircleReports(request, request.toCreatedAtDescPageable());
        return ResponseEntity.ok(PaginatedResponse.fromPage(circleReports));
    }

    @GetMapping("/reports/posts")
    public ResponseEntity<PaginatedResponse<PostReportResponse>> findPostReports(ReportFindRequest request) {
        Page<PostReportResponse> postReports = reportService.findPostReports(request, request.toCreatedAtDescPageable());
        return ResponseEntity.ok(PaginatedResponse.fromPage(postReports));
    }

    @PutMapping("/reports/{reportId}/handle")
    public ResponseEntity<SuccessResponse> handleReport(
            @PathVariable @Positive Long reportId
    ){
        reportService.handleReport(reportId);
        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }
}
