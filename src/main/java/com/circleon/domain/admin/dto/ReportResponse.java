package com.circleon.domain.admin.dto;

import com.circleon.domain.report.dto.ReportInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReportResponse {

    private ReportInfo reportInfo;

    public static ReportResponse of(ReportInfo reportInfo) {
        return ReportResponse.builder().reportInfo(reportInfo).build();
    }
}
