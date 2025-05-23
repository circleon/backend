package com.circleon.domain.admin.service;

import com.circleon.domain.report.dto.ReportInfo;
import com.circleon.domain.report.ReportType;
import com.circleon.domain.report.entity.Report;
import com.circleon.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public Page<ReportInfo> findReport(ReportType reportType, boolean handled, Pageable pageable) {
        Page<Report> reports = reportRepository.findReports(reportType, handled, pageable);
        return reports.map(ReportInfo::from);
    }
}
