package com.circleon.domain.admin.controller;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.report.dto.ReportInfo;
import com.circleon.domain.admin.dto.ReportResponse;
import com.circleon.domain.admin.service.AdminReportService;
import com.circleon.domain.report.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReportController {

    private final AdminReportService reportService;

    @GetMapping("/reports")
    public ResponseEntity<PaginatedResponse<ReportResponse>> findReports(@RequestParam ReportType reportType,
                                                                         @RequestParam boolean handled,
                                                                         Pageable pageable){
        Page<ReportInfo> reports = reportService.findReport(reportType, handled, pageable);
        return ResponseEntity.ok(PaginatedResponse.fromPage(reports.map(ReportResponse::of)));
    }
}
