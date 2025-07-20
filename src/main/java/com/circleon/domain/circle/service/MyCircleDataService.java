package com.circleon.domain.circle.service;

import com.circleon.domain.circle.entity.MyCircle;

import java.util.Optional;

public interface MyCircleDataService {

    Optional<MyCircle> findJoinedMember(Long userId, Long circleId);
}
