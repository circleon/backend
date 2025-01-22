package com.circleon.domain.circle.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.circle.dto.*;

import com.circleon.domain.circle.entity.MyCircle;

import java.util.Optional;

public interface MyCircleService {

    MyCircleCreateResponse applyForMembership(Long userId, Long circleId);

    PaginatedResponse<MyCircleSearchResponse> findPagedMyCircles(MyCircleSearchRequest myCircleSearchRequest);

    void deleteApplication(Long userId, Long memberId);

    void processLeaveRequest(Long userId, Long memberId, CircleLeaveRequest circleLeaveRequest);

}
