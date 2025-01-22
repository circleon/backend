package com.circleon.domain.schedule.circle.repository;

import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.schedule.circle.dto.CircleInfo;
import com.circleon.domain.schedule.circle.dto.CircleScheduleDetail;

import java.time.LocalDateTime;
import java.util.List;

public interface CircleScheduleRepositoryCustom {

    List<CircleScheduleDetail> findSchedulesWithinDateRange(Long circleId, LocalDateTime startAt, LocalDateTime endAt);

    CircleInfo findCircleInfo(Long circleId);

    void deleteAllByCircles(List<Circle> circles);
}
