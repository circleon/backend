package com.circleon.domain.admin.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.admin.dto.CircleResponse;
import com.circleon.domain.admin.dto.CircleUpdateOfficialRequest;
import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.CircleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCircleService {

    private final CircleRepository circleRepository;

    @Transactional(readOnly = true)
    public PaginatedResponse<CircleResponse> findPagedCirclesByOfficialStatus(OfficialStatus officialStatus,
                                                                              CategoryType categoryType,
                                                                              Pageable pageable){
        //조회
        Page<Circle> circles;
        if(categoryType == CategoryType.ALL){
            circles = circleRepository.findAllByCircleStatusAndOfficialStatus(CircleStatus.ACTIVE, officialStatus, pageable);
        }else {
            circles = circleRepository
                    .findAllByCircleStatusAndCategoryTypeAndOfficialStatus(CircleStatus.ACTIVE, categoryType, officialStatus, pageable);
        }

        Page<CircleResponse> circleResponses = circles.map(CircleResponse::from);

        return PaginatedResponse.fromPage(circleResponses);
    }

    @Transactional
    public void updateOfficialStatus(Long circleId, CircleUpdateOfficialRequest updateOfficialRequest){

        //조회
        Circle circle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND, "[updateOfficialStatus] circle not found"));

        circle.setOfficialStatus(updateOfficialRequest.getOfficialStatus());
    }

    @Transactional
    public void deleteCircle(Long circleId){
        Circle circle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND, "[updateOfficialStatus] circle not found"));

        circle.setCircleStatus(CircleStatus.INACTIVE);
    }
}
