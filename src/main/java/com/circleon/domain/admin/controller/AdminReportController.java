package com.circleon.domain.admin.controller;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.admin.dto.CircleReportResponse;

import com.circleon.domain.admin.dto.ReportFindRequest;
import com.circleon.domain.admin.service.AdminReportService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
