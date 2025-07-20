package com.circleon.domain.schedule.circle.service;

import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.schedule.circle.dto.*;
import com.circleon.domain.schedule.circle.entity.CircleSchedule;

import java.util.List;

public interface CircleScheduleService {

    CircleScheduleCreateResponse createCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, CircleScheduleCreateRequest circleScheduleCreateRequest);

    CircleMonthlySchedules findMonthlySchedules(CircleMemberIdentifier circleMemberIdentifier, int year, int month);

    CircleScheduleUpdateResponse updateCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, Long circleScheduleId, CircleScheduleUpdateRequest updateRequest);

    void deleteCircleSchedule(CircleMemberIdentifier circleMemberIdentifier, Long circleScheduleId);

    CircleScheduleDetail findNextSchedule(CircleMemberIdentifier circleMemberIdentifier);


}
