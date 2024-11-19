package com.circleon.domain.circle.service;

import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.MyCircleCreateRequest;
import com.circleon.domain.circle.dto.MyCircleCreateResponse;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.user.entity.User;

import java.util.Optional;

public interface MyCircleService {

    MyCircleCreateResponse applyForMembership(Long userId, Long circleId, MyCircleCreateRequest myCircleCreateRequest);

    Optional<MyCircle> findByUserAndCircleAndMembershipStatus(User user, Circle circle, MembershipStatus membershipStatus);
}
