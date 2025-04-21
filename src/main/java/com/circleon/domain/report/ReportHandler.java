package com.circleon.domain.report;

public interface ReportHandler {

    ReportType getType();

    void validateTargetExist(Long targetId);

}
