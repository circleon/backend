package com.circleon.domain.circle.service;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.dto.CircleCreateRequest;
import com.circleon.domain.circle.dto.CircleResponse;
import com.circleon.domain.circle.dto.CircleUpdateRequest;
import com.circleon.domain.circle.dto.CircleUpdateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CircleService {

    void createCircle(Long applicantId, CircleCreateRequest circleCreateRequest);

    Page<CircleResponse> findCircles(Long userId, Pageable pageable, CategoryType categoryType);

    CircleUpdateResponse updateCircle(Long userId, Long circleId, CircleUpdateRequest circleUpdateRequest);

}
