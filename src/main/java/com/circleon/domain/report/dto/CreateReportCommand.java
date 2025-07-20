package com.circleon.domain.report.dto;

import com.circleon.domain.report.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CreateReportCommand {

    private ReportType targetType;

    private Long targetId;

    private Long reporterId;

    private Long circleId;

    private String reason;

    public static CreateReportCommand of(ReportType targetType, Long targetId, Long reporterId, Long circleId, String reason) {
        return CreateReportCommand.builder()
                .targetType(targetType)
                .targetId(targetId)
                .reporterId(reporterId)
                .circleId(circleId)
                .reason(reason)
                .build();
    }
}
