package com.circleon.domain.schedule.circle.service;

import com.circleon.domain.circle.entity.Circle;

import java.util.List;

public interface CircleScheduleDataService {

    void deleteAllByCircles(List<Circle> circles);
}
