package com.circleon.domain.circle.service;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.dto.*;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CircleService {

    void createCircle(Long applicantId, CircleCreateRequest circleCreateRequest);

    Page<CircleResponse> findPagedCircles(Pageable pageable, CategoryType categoryType);

    CircleInfoUpdateResponse updateCircleInfo(Long userId, Long circleId, CircleInfoUpdateRequest circleInfoUpdateRequest);

    CircleImagesUpdateResponse updateCircleImages(Long userId, Long circleId, CircleImagesUpdateRequest circleImageUpdateRequest);

    void deleteCircleImages(Long userId, Long circleId, boolean deleteProfileImg, boolean deleteIntroImg);

    Resource loadImageAsResource(String filePath);

    CircleDetailResponse findCircleDetail(Long userId, Long circleId);

    List<CircleSimpleResponse> findAllCirclesSimple();

    //TODO 동아리 삭제
}
