package com.circleon.domain.schedule.circle.service;

import com.circleon.domain.schedule.circle.dto.*;

public interface CircleScheduleService {

    CircleScheduleCreateResponse createCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, CircleScheduleCreateRequest circleScheduleCreateRequest);

    CircleMonthlySchedules findMonthlySchedules(CircleMemberIdentifier circleMemberIdentifier, int year, int month);

    CircleScheduleUpdateResponse updateCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, Long circleScheduleId, CircleScheduleUpdateRequest updateRequest);

    void deleteCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, Long circleScheduleId);
}
