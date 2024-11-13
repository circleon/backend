package com.circleon.domain.circle.service;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.MembershipStatus;
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

    Page<CircleMemberResponse> findPagedCircleMembers(Long userid, Long circleId, Pageable pageable, MembershipStatus membershipStatus);

    //동아리 멤버 직책 변경
    void updateCircleMemberRole(Long userId, Long circleId, Long memberId, CircleRoleUpdateRequest circleRoleUpdateRequest);

    //동아리 멥머 가입 승인 거절 동시에
    void updateMembershipStatus(Long userId, Long circleId, Long memberId, Boolean approved);

    //동아리원 추방
    void expelMember(Long userId, Long circleId, Long memberId);

    //TODO 동아리 삭제
}
