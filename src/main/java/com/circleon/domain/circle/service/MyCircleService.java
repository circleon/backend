package com.circleon.domain.circle.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.entity.MyCircle;

import java.util.Optional;

public interface MyCircleService {

    MyCircleCreateResponse applyForMembership(Long userId, Long circleId, MyCircleCreateRequest myCircleCreateRequest);


    Optional<MyCircle> fineJoinedMember(Long userId, Long circleId);

    PaginatedResponse<MyCircleSearchResponse> findPagedMyCircles(MyCircleSearchRequest myCircleSearchRequest);
}
