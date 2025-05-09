package com.circleon.domain.circle.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.circle.CategoryType;

import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.dto.*;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CircleService {

    void createCircle(Long applicantId, CircleCreateRequest circleCreateRequest);

    PaginatedResponse<CircleResponse> findPagedCircles(Pageable pageable, CategoryType categoryType);

    CircleInfoUpdateResponse updateCircleInfo(Long userId, Long circleId, CircleInfoUpdateRequest circleInfoUpdateRequest);

    CircleDetailResponse findCircleDetail(Long userId, Long circleId);

    List<CircleSimpleResponse> findAllCirclesSimple();

    void updateOfficialStatus(Long userId, Long circleId, OfficialStatus officialStatus);

    void updateRecruitingStatus(Long userId, Long circleId, RecruitingStatusUpdateRequest recruitingStatusUpdateRequest);

    //TODO 동아리 삭제

    void deleteSoftDeletedCircles();

}
