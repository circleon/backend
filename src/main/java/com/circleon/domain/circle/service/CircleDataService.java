package com.circleon.domain.circle.service;

import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.entity.Circle;

import java.util.Optional;

public interface CircleDataService {

    Optional<Circle> findByIdAndCircleStatus(Long circleId, CircleStatus circleStatus);
}
