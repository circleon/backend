package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.*;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.circle.repository.MyCircleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CircleMemberService {

    private final MyCircleRepository myCircleRepository;
    private final CircleRepository circleRepository;
    private final CircleAuthValidator circleAuthValidator;

    @Transactional
    public void updateCircleMemberRole(Long userId, Long circleId, Long memberId, CircleRoleUpdateRequest circleRoleUpdateRequest) {

        // 회장만 가능
        MyCircle president = circleAuthValidator.validatePresidentAccess(userId, circleId);

        // 이 멤버가 실제 가입하고 있는지 체크
        MyCircle member = myCircleRepository.findByIdAndCircle(memberId, president.getCircle())
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND, "[updateCircleMemberRole] 멤버가 존재하지 않습니다."));

        if(circleRoleUpdateRequest.getCircleRole() == CircleRole.PRESIDENT){
            // 회장으로
            member.setCircleRole(circleRoleUpdateRequest.getCircleRole());

            // 본인은 임원으로
            president.setCircleRole(CircleRole.EXECUTIVE);
            return ;
        }

        if(circleRoleUpdateRequest.getCircleRole() != null){
            member.setCircleRole(circleRoleUpdateRequest.getCircleRole());
        }
    }

    @Transactional
    public void updateMembershipStatus(Long userId, Long circleId, Long memberId, MembershipStatusUpdateRequest membershipStatusUpdateRequest) {

        //임원들 가능
        MyCircle executive = circleAuthValidator.validateExecutiveAccess(userId, circleId, "[updateMembershipStatus] 멤버가 아닙니다.");

        //상태가 변경될 멤버
        MyCircle member = myCircleRepository.findByIdAndCircle(memberId, executive.getCircle())
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
            registerMember(executive.getCircle(), member);
            return;
        }

        //탈퇴 승인 시
        if(membershipStatus == MembershipStatus.INACTIVE){
            leaveCircle(executive.getCircle());
        }

    }

    private void registerMember(Circle circle, MyCircle member) {
        circle.incrementMemberCount();
        member.initJoinedAt();
        member.setCircleRole(CircleRole.MEMBER);
    }

    private void leaveCircle(Circle circle) {
        circle.decrementMemberCount();
    }

    @Transactional
    public void expelMember(Long userId, Long circleId, Long memberId) {

        //임원들 가능

        MyCircle executive = circleAuthValidator.validateExecutiveAccess(userId, circleId, "[expelMember] 멤버가 아닙니다.");

        //실제 가입되어 있는 유저인지
        MyCircle member = myCircleRepository.findByIdAndCircleAndMembershipStatus(memberId, executive.getCircle(), MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND));

        if(member.getCircleRole() == CircleRole.PRESIDENT){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[expelMember] 회장은 추방 불가능");
        }

        if(member.getUser().getId().equals(userId)){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[expelMember] 본인은 추방 불가능");
        }

        //추방 + 카운트
        member.setMembershipStatus(MembershipStatus.INACTIVE);
        executive.getCircle().decrementMemberCount();
    }

    @Transactional(readOnly = true)
    public CircleLeaveMessage findLeaveMessage(Long userId, Long circleId, Long memberId) {

        //임원인지 체크
        MyCircle executive = circleAuthValidator.validateExecutiveAccess(userId, circleId, "[findLeaveMessage] 임원이 아닙니다.");

        //멤버가 가입되어 있고 탈퇴 요청 생태인지
        MyCircle member = myCircleRepository.findByIdAndCircleAndMembershipStatus(memberId, executive.getCircle(), MembershipStatus.LEAVE_REQUEST)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND));

        return CircleLeaveMessage.of(member.getLeaveMessage());
    }

    @Transactional(readOnly = true)
    public CircleJoinMessage findJoinMessage(Long userId, Long circleId, Long memberId) {

        //임원인지 체크
        MyCircle executive = circleAuthValidator.validateExecutiveAccess(userId, circleId, "[findJoinMessage] 임원이 아닙니다.");

        //멤버가 가입대기 상태인지
        MyCircle member = myCircleRepository.findByIdAndCircleAndMembershipStatus(memberId, executive.getCircle(), MembershipStatus.PENDING)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND));

        return CircleJoinMessage.of(member.getJoinMessage());
    }

    @Transactional(readOnly = true)
    public Page<CircleMemberResponse> findPagedCircleMembers(Long userId, Long circleId, Pageable pageable, MembershipStatus membershipStatus) {

        //회원이면 가능하도록
        Optional<MyCircle> optionalMember = myCircleRepository.findJoinedMember(userId, circleId);

        //동아리 원이 아닌경우는 가입자만
        if(optionalMember.isEmpty() && membershipStatus != MembershipStatus.APPROVED){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[findPagedCircleMembers] 동아리원이 아닌 경우 가입자 명단만 조회 가능");
        }

        optionalMember.ifPresent(m->{
            if(m.getCircleRole() == CircleRole.MEMBER && membershipStatus != MembershipStatus.APPROVED){
                throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "동아리원은 가입자 명단만 조회가 가능합니다.");
            }
        });

        //TODO 가입자 명단, 가입신청자 명단, 탈퇴 신청자 명단 폼이 다 다를거 같은데


        return optionalMember.map(member -> myCircleRepository
                        .findAllByCircleAndMembershipStatusWithUser(member.getCircle(), membershipStatus, pageable)
                        .map(CircleMemberResponse::fromMyCircle))
                .orElseGet(()-> {
                    Circle circle = circleRepository.findById(circleId)
                            .orElseThrow(() -> new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND, "[findPagedCircleMembers] 존재하지 않는 동아리"));

                    return myCircleRepository.findAllByCircleAndMembershipStatusWithUser(circle, membershipStatus, pageable)
                            .map(CircleMemberResponse::fromMyCircle);
                });
    }
}
