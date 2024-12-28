package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.MyCircleRepository;
import com.circleon.domain.user.entity.User;

import com.circleon.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyCircleServiceImpl implements MyCircleService {

    private final MyCircleRepository myCircleRepository;
    private final CircleService circleService;
    private final UserService userService;

    @Override
    public MyCircleCreateResponse applyForMembership(Long userId, Long circleId) {

        //이미 가입되어 있는지 확인
        User user = userService.findById(userId)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        Circle circle = circleService.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(()->new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        List<MembershipStatus> membershipStatuses = new ArrayList<>();

        membershipStatuses.add(MembershipStatus.APPROVED);
        membershipStatuses.add(MembershipStatus.PENDING);
        membershipStatuses.add(MembershipStatus.LEAVE_REQUEST);

        myCircleRepository.findAllByUserAndCircleInMembershipStatuses(user, circle, membershipStatuses)
                .ifPresent(myCircle -> validateMembershipStatusAndThrowException(myCircle.getMembershipStatus()));

        //가입 신청
        MyCircle applicant = MyCircle.builder()
                .user(user)
                .circle(circle)
                .circleRole(CircleRole.MEMBER)
                .membershipStatus(MembershipStatus.PENDING)
                .build();

        MyCircle savedMyCircle = myCircleRepository.save(applicant);

        return MyCircleCreateResponse.fromMyCircle(savedMyCircle);
    }

    private void validateMembershipStatusAndThrowException(MembershipStatus membershipStatus) {

        if (membershipStatus.equals(MembershipStatus.APPROVED)|| membershipStatus.equals(MembershipStatus.LEAVE_REQUEST)) {
            throw new CircleException(CircleResponseStatus.ALREADY_MEMBER, "[applyForMembership] 이미 가입된 동아리");
        }

        if(membershipStatus.equals(MembershipStatus.PENDING)) {
            throw new CircleException(CircleResponseStatus.ALREADY_APPLIED, "[applyForMembership] 이미 가입 신청한 상태");
        }

    }

    @Override
    public PaginatedResponse<MyCircleSearchResponse> findPagedMyCircles(MyCircleSearchRequest myCircleSearchRequest) {

        // 가입 명단 조회
        Page<MyCircleSearchResponse> pagedMyCircles = myCircleRepository
                .findAllByMyCircleSearchRequest(myCircleSearchRequest);

        return PaginatedResponse.fromPage(pagedMyCircles);
    }

    @Override
    public Optional<MyCircle> fineJoinedMember(Long userId, Long circleId) {
        return myCircleRepository.fineJoinedMember(userId, circleId);
    }


    @Override
    public void deleteApplication(Long userId, Long memberId) {

        //존재하는지 확인
        MyCircle member = myCircleRepository.findByIdWithUserAndCircle(memberId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.APPLICATION_NOT_FOUND,
                        "[deleteApplication] 가입 신청이 존재하지 않음."));

        //해당 유저가 맞는지 체크
        validateOwnership(userId, member, "[deleteApplication] 다른 유저의 동아리 가입 신청 삭제 시도");

        //pending 즉 가입 요청 상태인지 체크;
        if(member.getMembershipStatus() != MembershipStatus.PENDING){
            throw new CircleException(CircleResponseStatus.APPLICATION_NOT_FOUND,
                    "[deleteApplication] 가입 신청이 존재하지 않음.");
        }

        member.setMembershipStatus(MembershipStatus.INACTIVE);

    }

    private void validateOwnership(Long userId, MyCircle member, String message) {
        if(!member.getUser().getId().equals(userId)){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, message);
        }
    }

    @Override
    public void processLeaveRequest(Long userId, Long memberId, CircleLeaveRequest circleLeaveRequest) {

        //존재하는지 확인
        MyCircle member = myCircleRepository.findByIdWithUserAndCircle(memberId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND,
                        "[processLeaveRequest] 존재하지 않은 동아리 회원"));

        //해당 유저가 맞는지 체크
        validateOwnership(userId, member, "[processLeaveRequest] 다른 유저의 동아리 탈퇴 신청 시도");

        //가입상태인지 체크
        if(member.getMembershipStatus() != MembershipStatus.APPROVED){
            throw new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND,
                    "[processLeaveRequest] 가입하지 않은 동아리");
        }

        //상태 변경 및 탈퇴 요청 메시지 저장
        member.setMembershipStatus(MembershipStatus.LEAVE_REQUEST);
        member.setLeaveMessage(circleLeaveRequest.getLeaveMessage());
    }
}
