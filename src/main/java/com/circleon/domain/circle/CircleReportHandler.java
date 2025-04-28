package com.circleon.domain.circle;

import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.report.ReportHandler;
import com.circleon.domain.report.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CircleReportHandler implements ReportHandler {

    private final CircleRepository circleRepository;

    @Override
    public ReportType getType() {
        return ReportType.CIRCLE;
    }

    @Override
    public void validateTargetExist(Long targetId) {

        if(!circleRepository.existsById(targetId)) {
            throw new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND, "[CircleReportHandler] - 동아리가 존재하지 않습니다.");
        }
    }
}
