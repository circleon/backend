package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;

import com.circleon.domain.circle.*;
import com.circleon.domain.circle.dto.CircleCreateRequest;
import com.circleon.domain.circle.dto.CircleResponse;
import com.circleon.domain.circle.dto.CircleUpdateRequest;
import com.circleon.domain.circle.dto.CircleUpdateResponse;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.circle.repository.MyCircleRepository;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CircleServiceImpl implements CircleService {

    private final CircleRepository circleRepository;
    private final MyCircleRepository myCircleRepository;
    private final UserService userService;
    private final FileStore circleFileStore;

    @Override
    public void createCircle(Long applicantId, CircleCreateRequest circleCreateRequest) {

        //존재하는 유저인지 검증
        User foundUser = userService.findByIdAndStatus(applicantId, UserStatus.ACTIVE);

        String imgPath = null;
        String thumbnailPath = null;

        //이미지 유효할때
        if(circleFileStore.isValidFile(circleCreateRequest.getProfileImg())) {

            //이미지 원본 저장
            imgPath = circleFileStore.storeFile(circleCreateRequest.getProfileImg());
            if (imgPath == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }

            //썸네일 저장
            thumbnailPath = circleFileStore.storeThumbnail(circleCreateRequest.getProfileImg());
            if (thumbnailPath == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }

        }


        //동아리 엔티티
        Circle circle = Circle.builder()
                .applicant(foundUser)
                .name(circleCreateRequest.getCircleName())
                .introduction(circleCreateRequest.getIntroduction())
                .circleStatus(CircleStatus.PENDING)
                .profileImgUrl(imgPath)
                .thumbnailUrl(thumbnailPath)
                .categoryType(circleCreateRequest.getCategory())
                .build();

        circleRepository.save(circle);

    }

    @Override
    public Page<CircleResponse> findCircles(Long userId, Pageable pageable, CategoryType categoryType) {

        if(!userService.existsByIdAndStatus(userId, UserStatus.ACTIVE)){
            throw new CommonException(CommonResponseStatus.LOGIN_REQUIRED);
        }

        if(categoryType == null){
            return circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE, pageable)
                        .map(circle -> {
                            int memberCount = myCircleRepository.countByCircleAndMembershipStatus(circle, MembershipStatus.APPROVED);
                            return CircleResponse.fromCircle(circle, memberCount);
                        });
        }else{
            return circleRepository.findAllByCategoryTypeAndCircleStatus(categoryType, CircleStatus.ACTIVE, pageable)
                        .map(circle -> {
                            int memberCount = myCircleRepository.countByCircleAndMembershipStatus(circle, MembershipStatus.APPROVED);
                            return CircleResponse.fromCircle(circle, memberCount);
                        });
        }
    }

    @Override
    public CircleUpdateResponse updateCircle(Long userId, Long circleId, CircleUpdateRequest circleUpdateRequest) {

        // 존재하는 써클인지
        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        User foundUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE);

        // 요청 유저가 해당 써클의 회장이나 임원인지? TODO 권한 뭐로 할지 회의 -> 회장만
        MyCircle foundMyCircle = myCircleRepository.findByUserAndCircleAndMembershipStatus(foundUser, foundCircle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));

        if (foundMyCircle.getCircleRole() == CircleRole.MEMBER) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS);
        }

        // 이미지 수정 여부 체크

        String imgPath;
        String thumbnailPath;

        if(circleFileStore.isValidFile(circleUpdateRequest.getProfileImg())) {

            //이미지 삭제
            boolean isDeletedImg = circleFileStore.deleteFile(foundCircle.getProfileImgUrl());

            if(!isDeletedImg) {
                log.warn("동아리 프로필 이미지 파일 삭제 오류");
            }

            //이미지 원본 저장
            imgPath = circleFileStore.storeFile(circleUpdateRequest.getProfileImg());
            if (imgPath == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }

            boolean isDeletedThumbNail = circleFileStore.deleteFile(foundCircle.getThumbnailUrl());

            if(!isDeletedThumbNail) {
                log.warn("동아리 프로필 이미지 썸네일 파일 삭제 오류");
            }

            //썸네일 저장
            thumbnailPath = circleFileStore.storeThumbnail(circleUpdateRequest.getProfileImg());
            if (thumbnailPath == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            imgPath = foundCircle.getProfileImgUrl();
            thumbnailPath = foundCircle.getThumbnailUrl();
        }

        //수정
        foundCircle.setName(circleUpdateRequest.getCircleName());
        foundCircle.setCategoryType(circleUpdateRequest.getCategoryType());
        foundCircle.setProfileImgUrl(imgPath);
        foundCircle.setThumbnailUrl(thumbnailPath);

        return CircleUpdateResponse.builder()
                .circleId(foundCircle.getId())
                .circleName(foundCircle.getName())
                .category(foundCircle.getCategoryType())
                .profileImgUrl(foundCircle.getProfileImgUrl())
                .thumbnailUrl(foundCircle.getThumbnailUrl())
                .build();
    }

}
