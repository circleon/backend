package com.circleon.domain.admin.dto;

public record PostReportResponse(
        ReportInfo reportInfo,
        PostInfo postInfo
) {
    public static PostReportResponse from(ReportInfo reportInfo, PostInfo postInfo) {
        return new PostReportResponse(reportInfo, postInfo);
    }
}
