package com.circleon.domain.admin.dto;

import com.circleon.domain.report.ReportType;
import com.circleon.domain.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ReportInfo {

    private Long reportId;

    private Long targetId;

    private ReportType targetType;

    private String reason;

    private boolean handled;

    private Reporter reporter;

    public static ReportInfo from(Report report) {
        return ReportInfo.builder()
                .reportId(report.getId())
                .targetId(report.getTargetId())
                .targetType(report.getTargetType())
                .reason(report.getReason())
                .handled(report.isHandled())
                .reporter(
                        Reporter.of(report.getReporter().getId(), report.getReporter().getUsername())
                )
                .build();

    }
}
