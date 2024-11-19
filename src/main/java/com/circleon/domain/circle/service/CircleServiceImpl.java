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
        User foundUser = userService.findByIdAndStatus(applicantId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));


        String profileImgUrl = null;
        String thumbnailUrl = null;

        //동아리 엔티티
        Circle circle = Circle.builder()
                .applicant(foundUser)
                .name(circleCreateRequest.getCircleName())
                .introduction(circleCreateRequest.getIntroduction())
                .circleStatus(CircleStatus.PENDING)
                .categoryType(circleCreateRequest.getCategory())
                .profileImgUrl(profileImgUrl)
                .thumbnailUrl(thumbnailUrl)
                .build();

        Circle savedCircle = circleRepository.save(circle);

        //이미지 유효할때
        if(circleFileStore.isValidFile(circleCreateRequest.getProfileImg())) {

            //이미지 원본 저장
            profileImgUrl = circleFileStore.storeFile(circleCreateRequest.getProfileImg(), savedCircle.getId());
            if (profileImgUrl == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }

            //썸네일 저장
            thumbnailUrl = circleFileStore.storeThumbnail(circleCreateRequest.getProfileImg(), savedCircle.getId());
            if (thumbnailUrl == null) {
                throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
            }

            savedCircle.setProfileImgUrl(profileImgUrl);
            savedCircle.setThumbnailUrl(thumbnailUrl);

        }

    }

    @Override
    public Page<CircleResponse> findPagedCircles(Pageable pageable, CategoryType categoryType) {

        if(categoryType == null){
            return circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE, pageable)
                        .map(CircleResponse::fromCircle);
        }else{
            return circleRepository.findAllByCategoryTypeAndCircleStatus(categoryType, CircleStatus.ACTIVE, pageable)
                        .map(CircleResponse::fromCircle);
        }
    }

    @Override
    public CircleInfoUpdateResponse updateCircleInfo(Long userId, Long circleId, CircleInfoUpdateRequest circleInfoUpdateRequest) {

        User presidentUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        // 수정 권한 체크
        Circle foundCircle = validatePresidentAccess(presidentUser, circleId);

        //수정
        foundCircle.setName(circleInfoUpdateRequest.getCircleName());
        foundCircle.setCategoryType(circleInfoUpdateRequest.getCategoryType());
        foundCircle.setIntroduction(circleInfoUpdateRequest.getIntroduction());
        foundCircle.setRecruitmentStartDate(circleInfoUpdateRequest.getRecruitmentStartDate());
        foundCircle.setRecruitmentEndDate(circleInfoUpdateRequest.getRecruitmentEndDate());
        foundCircle.setSummary(circleInfoUpdateRequest.getSummary());

        return CircleInfoUpdateResponse.fromCircle(foundCircle);
    }

    private Circle validatePresidentAccess(User user, Long circleId) {
        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        // 요청 유저가 해당 써클의 회장이나 임원인지? 회장만
        MyCircle foundMyCircle = myCircleRepository.findByUserAndCircleAndMembershipStatus(user, foundCircle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));


        if (foundMyCircle.getCircleRole() != CircleRole.PRESIDENT) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS);
        }
        return foundCircle;
    }

    @Override
    public CircleImagesUpdateResponse updateCircleImages(Long userId, Long circleId, CircleImagesUpdateRequest circleImageUpdateRequest) {

        User presidentUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        Circle foundCircle = validatePresidentAccess(presidentUser, circleId);

        // 이미지 저장 null값이면 수정 안함

        //프로필 사진
        if(circleFileStore.isValidFile(circleImageUpdateRequest.getProfileImg())) {

            //기존 사진들 삭제
            deleteImg(foundCircle.getProfileImgUrl());

            deleteImg(foundCircle.getThumbnailUrl());

            //저장
            String profileImgUrl = storeImg(circleImageUpdateRequest.getProfileImg(), foundCircle.getId());

            String thumbnailUrl = storeThumbnail(circleImageUpdateRequest.getProfileImg(), foundCircle.getId());

            foundCircle.setProfileImgUrl(profileImgUrl);
            foundCircle.setThumbnailUrl(thumbnailUrl);

        }

        //소개글 사진
        if(circleFileStore.isValidFile(circleImageUpdateRequest.getIntroImg())){

            deleteImg(foundCircle.getIntroImgUrl());

            String introImgUrl = storeImg(circleImageUpdateRequest.getIntroImg(), foundCircle.getId());

            foundCircle.setIntroImgUrl(introImgUrl);

        }

        return CircleImagesUpdateResponse.fromCircle(foundCircle);
    }

    private String storeImg(MultipartFile file, Long circleId) {
        String profileImgUrl = circleFileStore.storeFile(file, circleId);

        if(profileImgUrl == null){
            throw new CommonException(CommonResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return profileImgUrl;
    }

    private String storeThumbnail(MultipartFile file, Long circleId) {
        String thumbnailUrl = circleFileStore.storeThumbnail(file, circleId);
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

        User presidentUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        //권한 체크
        Circle foundCircle = validatePresidentAccess(presidentUser, circleId);

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

        User foundUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        //존재하는 써클인지
        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));


        int memberCount = myCircleRepository.countByCircleAndMembershipStatus(foundCircle, MembershipStatus.APPROVED);

        //목록 조회시 카운트
        foundCircle.setMemberCount(memberCount);

        //현재 써클에 가입유무 후 가입 -> 역할까지
        Optional<MyCircle> foundMyCircle = myCircleRepository.findByUserAndCircleAndMembershipStatus(foundUser, foundCircle, MembershipStatus.APPROVED);

        return foundMyCircle.map(myCircle -> CircleDetailResponse.fromCircle(foundCircle,myCircle.getCircleRole(), myCircle.getId()))
                .orElseGet(()-> CircleDetailResponse.fromCircle(foundCircle, null, null));

    }

    @Override
    public List<CircleSimpleResponse> findAllCirclesSimple() {

        List<Circle> circles = circleRepository.findAllByCircleStatus(CircleStatus.ACTIVE);

        return circles.stream().map(CircleSimpleResponse::fromCircle).toList();
    }

    @Override
    public Page<CircleMemberResponse> findPagedCircleMembers(Long userId, Long circleId, Pageable pageable, MembershipStatus membershipStatus) {

        User user = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        //회원이면 가능하도록
        MyCircle member = validateMemberAccess(user, circleId);

        //회원이면 가입 명단만 가능하도록 해야할듯?
        if(member.getCircleRole() == CircleRole.MEMBER && membershipStatus != MembershipStatus.APPROVED){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "동아리원은 가입자 명단만 조회가 가능합니다.");
        }

        return myCircleRepository.findAllByCircleAndMembershipStatusWithUser(member.getCircle(), membershipStatus, pageable)
                .map(CircleMemberResponse::fromMyCircle);
    }

    private MyCircle validateMemberAccess(User user, Long circleId) {

        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        return myCircleRepository.findByUserAndCircleAndMembershipStatus(user, foundCircle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));
    }


    private Circle validateExecutiveAccess(User user, Long circleId) {

        Circle foundCircle = circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        MyCircle foundMyCircle = myCircleRepository.findByUserAndCircleAndMembershipStatus(user, foundCircle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));

        if(foundMyCircle.getCircleRole() == CircleRole.MEMBER){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "동아리 임원이 아닙니다.");
        }

        return foundCircle;
    }

    @Override
    public void updateCircleMemberRole(Long userId, Long circleId, Long memberId, CircleRoleUpdateRequest circleRoleUpdateRequest) {

        User presidentUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        // 회장만 가능
        Circle targetCircle = validatePresidentAccess(presidentUser, circleId);

        // 이 멤버가 실제 가입하고 있는지 체크
        MyCircle member = myCircleRepository.findByIdAndCircleAndMembershipStatus(memberId, targetCircle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND));

        if(circleRoleUpdateRequest.getCircleRole() == CircleRole.PRESIDENT){
            // 회장으로
            member.setCircleRole(circleRoleUpdateRequest.getCircleRole());

            // 본인은 임원으로
            MyCircle president = myCircleRepository.findByUserAndCircleAndMembershipStatus(presidentUser, targetCircle, MembershipStatus.APPROVED)
                    .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));

            president.setCircleRole(CircleRole.EXECUTIVE);
            return ;
        }

        member.setCircleRole(circleRoleUpdateRequest.getCircleRole());
    }

    @Override
    public void updateMembershipStatus(Long userId, Long circleId, Long memberId, MembershipStatusUpdateRequest membershipStatusUpdateRequest) {

        //임원들 가능
        User authorizedUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        Circle circle = validateExecutiveAccess(authorizedUser, circleId);

        //상태가 변경될 멤버
        MyCircle member = myCircleRepository.findByIdAndCircle(memberId, circle)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND));

        //거절이나 탈퇴되어 있으면 변경 불가능
        if(member.getMembershipStatus() == MembershipStatus.INACTIVE
                || member.getMembershipStatus() == MembershipStatus.REJECTED){
            throw new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND, "[updateMembershipStatus] 동아리 멤버가 아닙니다.");
        }

        if(member.getCircleRole() == CircleRole.PRESIDENT){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[updateMembershipStatus] 회장은 변경 불가능합니다.");
        }

        if(member.getUser().getId().equals(userId)){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[updateMembershipStatus] 본인은 변경 불가능합니다.");
        }

        if(member.getMembershipStatus() == membershipStatusUpdateRequest.getMembershipStatus()){
            throw new CommonException(CommonResponseStatus.BAD_REQUEST, "[updateMembershipStatus] 이미 같은 가입 상태입니다.");
        }

        member.setMembershipStatus(membershipStatusUpdateRequest.getMembershipStatus());

        MembershipStatus membershipStatus = member.getMembershipStatus();

        // 가입시 카운트
        if(membershipStatus == MembershipStatus.APPROVED){
            registerMember(circle, member);
            return;
        }

        //탈퇴 승인 시
        if(membershipStatus == MembershipStatus.INACTIVE){
            leaveCircle(circle);
        }

    }

    private void registerMember(Circle circle, MyCircle member) {
        circle.incrementMemberCount();
        member.initJoinedAt();
    }

    private void leaveCircle(Circle circle) {
        circle.decrementMemberCount();
    }


    @Override
    public void expelMember(Long userId, Long circleId, Long memberId) {

        //임원들 가능
        User authorizedUser = userService.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        Circle circle = validateExecutiveAccess(authorizedUser, circleId);

        //실제 가입되어 있는 유저인지
        MyCircle member = myCircleRepository.findByIdAndCircleAndMembershipStatus(memberId, circle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND));

        if(member.getCircleRole() == CircleRole.PRESIDENT){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[expelMember] 회장은 추방 불가능");
        }

        if(member.getUser().getId().equals(userId)){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[expelMember] 본인은 추방 불가능");
        }

        //추방 + 카운트
        member.setMembershipStatus(MembershipStatus.INACTIVE);
        circle.decrementMemberCount();
    }

    @Override
    public Optional<Circle> findByIdAndCircleStatus(Long circleId, CircleStatus circleStatus) {
        return circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE);
    }
}
