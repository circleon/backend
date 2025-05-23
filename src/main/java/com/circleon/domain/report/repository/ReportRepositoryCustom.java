package com.circleon.domain.report.repository;

import com.circleon.domain.report.ReportType;
import com.circleon.domain.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportRepositoryCustom {

    Page<Report> findReports(ReportType reportType, boolean handled, Pageable pageable);
}
