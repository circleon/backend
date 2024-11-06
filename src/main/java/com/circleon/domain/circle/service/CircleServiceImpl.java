package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;

import com.circleon.domain.circle.*;
import com.circleon.domain.circle.dto.*;
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
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


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

        String profileImgUrl = null;
        String thumbnailUrl = null;

        //이미지 유효할때
        if(circleFileStore.isValidFile(circleCreateRequest.getProfileImg())) {

            //이미지 원본 저장
            profileImgUrl = circleFileStore.storeFile(circleCreateRequest.getProfileImg());
            if (profileImgUrl == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }

            //썸네일 저장
            thumbnailUrl = circleFileStore.storeThumbnail(circleCreateRequest.getProfileImg());
            if (thumbnailUrl == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }

        }


        //동아리 엔티티
        Circle circle = Circle.builder()
                .applicant(foundUser)
                .name(circleCreateRequest.getCircleName())
                .introduction(circleCreateRequest.getIntroduction())
                .circleStatus(CircleStatus.PENDING)
                .profileImgUrl(profileImgUrl)
                .thumbnailUrl(thumbnailUrl)
                .categoryType(circleCreateRequest.getCategory())
                .build();

        circleRepository.save(circle);

    }

    @Override
    public Page<CircleResponse> findPagedCircles(Pageable pageable, CategoryType categoryType) {

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
    public CircleInfoUpdateResponse updateCircleInfo(Long userId, Long circleId, CircleInfoUpdateRequest circleInfoUpdateRequest) {

        // 수정 권한 체크
        Circle foundCircle = validatePresidentAccess(userId, circleId);

        //수정
        foundCircle.setName(circleInfoUpdateRequest.getCircleName());
        foundCircle.setCategoryType(circleInfoUpdateRequest.getCategoryType());
        foundCircle.setIntroduction(circleInfoUpdateRequest.getIntroduction());
        foundCircle.setRecruitmentStartDate(circleInfoUpdateRequest.getRecruitmentStartDate());
        foundCircle.setRecruitmentEndDate(circleInfoUpdateRequest.getRecruitmentEndDate());

        return CircleInfoUpdateResponse.fromCircle(foundCircle);
    }

    private Circle validatePresidentAccess(Long userId, Long circleId) {
        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        User foundUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE);

        // 요청 유저가 해당 써클의 회장이나 임원인지? 회장만
        MyCircle foundMyCircle = myCircleRepository.findByUserAndCircleAndMembershipStatus(foundUser, foundCircle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));

        if (foundMyCircle.getCircleRole() != CircleRole.PRESIDENT) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS);
        }
        return foundCircle;
    }

    @Override
    public CircleImagesUpdateResponse updateCircleImages(Long userId, Long circleId, CircleImagesUpdateRequest circleImageUpdateRequest) {

        Circle foundCircle = validatePresidentAccess(userId, circleId);

        // 이미지 저장 null값이면 수정 안함

        //프로필 사진
        if(circleFileStore.isValidFile(circleImageUpdateRequest.getProfileImg())) {

            //기존 사진들 삭제
            deleteImg(foundCircle.getProfileImgUrl());

            deleteImg(foundCircle.getThumbnailUrl());

            //저장
            String profileImgUrl = storeImg(circleImageUpdateRequest.getProfileImg());

            String thumbnailUrl = storeThumbnail(circleImageUpdateRequest.getProfileImg());

            foundCircle.setProfileImgUrl(profileImgUrl);
            foundCircle.setThumbnailUrl(thumbnailUrl);

        }

        //소개글 사진
        if(circleFileStore.isValidFile(circleImageUpdateRequest.getIntroImg())){

            deleteImg(foundCircle.getIntroImgUrl());

            String introImgUrl = storeImg(circleImageUpdateRequest.getIntroImg());

            foundCircle.setIntroImgUrl(introImgUrl);

        }

        return CircleImagesUpdateResponse.fromCircle(foundCircle);
    }

    private String storeImg(MultipartFile file) {
        String profileImgUrl = circleFileStore.storeFile(file);

        if(profileImgUrl == null){
            throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return profileImgUrl;
    }

    private String storeThumbnail(MultipartFile file) {
        String thumbnailUrl = circleFileStore.storeThumbnail(file);
        if(thumbnailUrl == null){
            throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return thumbnailUrl;
    }

    private void deleteImg(String imgUrl) {

        if(imgUrl == null){
            return ;
        }

        boolean isDeletedImg = circleFileStore.deleteFile(imgUrl);
        if(!isDeletedImg){
            log.warn("동아리 파일 삭제 실패 {}", imgUrl);
        }
    }

    @Override
    public void deleteCircleImages(Long userId, Long circleId, boolean deleteProfileImg, boolean deleteIntroImg) {

        //권한 체크
        Circle foundCircle = validatePresidentAccess(userId, circleId);

        //이미지 삭제
        if(deleteProfileImg){
            deleteImg(foundCircle.getProfileImgUrl());
            deleteImg(foundCircle.getThumbnailUrl());

            foundCircle.setProfileImgUrl(null);
            foundCircle.setThumbnailUrl(null);
        }

        if(deleteIntroImg){
            deleteImg(foundCircle.getIntroImgUrl());

            foundCircle.setIntroImgUrl(null);
        }
    }

    @Override
    public Resource loadImageAsResource(String filePath) {
        return circleFileStore.loadFileAsResource(filePath);
    }

    @Override
    public CircleDetailResponse findCircleDetail(Long userId, Long circleId) {

        User foundUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE);

        //존재하는 써클인지
        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));


        int memberCount = myCircleRepository.countByCircleAndMembershipStatus(foundCircle, MembershipStatus.APPROVED);

        //현재 써클에 가입유무 후 가입 -> 역할까지
        Optional<MyCircle> foundMyCircle = myCircleRepository.findByUserAndCircleAndMembershipStatus(foundUser, foundCircle, MembershipStatus.APPROVED);

        return foundMyCircle.map(myCircle -> CircleDetailResponse.fromCircle(foundCircle, memberCount, true, myCircle.getCircleRole()))
                .orElseGet(()-> CircleDetailResponse.fromCircle(foundCircle, memberCount, false, null));

    }

    @Override
    public List<CircleSimpleResponse> findAllCirclesSimple() {

        List<Circle> circles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);

        return circles.stream().map(CircleSimpleResponse::fromCircle).toList();
    }
}
