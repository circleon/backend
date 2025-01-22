package com.circleon.domain.circle.service;

import com.circleon.domain.circle.entity.MyCircle;

import java.util.Optional;

public interface MyCircleDataService {

    Optional<MyCircle> fineJoinedMember(Long userId, Long circleId);
}
