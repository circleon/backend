package com.circleon.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CircleReportResponse {

    private ReportInfo reportInfo;

    private CircleInfo circleInfo;

    public static CircleReportResponse from(ReportInfo reportInfo, CircleInfo circleInfo) {
        return CircleReportResponse.builder()
                .reportInfo(reportInfo)
                .circleInfo(circleInfo)
                .build();
    }
}
