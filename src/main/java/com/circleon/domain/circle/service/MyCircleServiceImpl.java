package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.MyCircleCreateRequest;
import com.circleon.domain.circle.dto.MyCircleCreateResponse;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.MyCircleRepository;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.User;

import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public MyCircleCreateResponse applyForMembership(Long userId, Long circleId, MyCircleCreateRequest myCircleCreateRequest) {

        //이미 가입되어 있는지 확인
        User user = userService.findById(userId)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        Circle circle = circleService.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(()->new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        List<MembershipStatus> membershipStatuses = new ArrayList<>();
        membershipStatuses.add(MembershipStatus.APPROVED);
        membershipStatuses.add(MembershipStatus.PENDING);

        myCircleRepository.findAllByUserAndCircleInMembershipStatuses(user, circle, membershipStatuses)
                .ifPresent(myCircle -> validateMembershipStatusAndThrowException(myCircle.getMembershipStatus()));

        //가입 신청
        MyCircle applicant = MyCircle.builder()
                .user(user)
                .circle(circle)
                .joinMessage(myCircleCreateRequest.getJoinMessage())
                .circleRole(CircleRole.MEMBER)
                .membershipStatus(MembershipStatus.PENDING)
                .build();

        MyCircle savedMyCircle = myCircleRepository.save(applicant);

        return MyCircleCreateResponse.fromMyCircle(savedMyCircle);
    }

    private void validateMembershipStatusAndThrowException(MembershipStatus membershipStatus) {

        if (membershipStatus.equals(MembershipStatus.APPROVED)) {
            throw new CircleException(CircleResponseStatus.ALREADY_MEMBER, "[applyForMembership] 이미 가입된 동아리");
        }

        if(membershipStatus.equals(MembershipStatus.PENDING)){
            throw new CircleException(CircleResponseStatus.ALREADY_APPLIED, "[applyForMembership] 이미 가입 신청한 상태");
        }

    }

    @Override
    public Optional<MyCircle> findByUserAndCircleAndMembershipStatus(User user, Circle circle, MembershipStatus membershipStatus) {
        return myCircleRepository.findByUserAndCircleAndMembershipStatus(user, circle, membershipStatus);
    }
}
