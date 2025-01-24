package com.circleon.domain.circle.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.CircleRoleUpdateRequest;
import com.circleon.domain.circle.dto.MembershipStatusUpdateRequest;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.circle.repository.MyCircleRepository;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CircleMemberService {

    private final CircleRepository circleRepository;
    private final UserDataService userDataService;
    private final MyCircleRepository myCircleRepository;

    public void updateCircleMemberRole(Long userId, Long circleId, Long memberId, CircleRoleUpdateRequest circleRoleUpdateRequest) {

        User presidentUser = userDataService.findByIdAndStatus(userId, UserStatus.ACTIVE)
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

        if(circleRoleUpdateRequest.getCircleRole() != null){
            member.setCircleRole(circleRoleUpdateRequest.getCircleRole());
        }
    }

    public void updateMembershipStatus(Long userId, Long circleId, Long memberId, MembershipStatusUpdateRequest membershipStatusUpdateRequest) {

        //임원들 가능
        User authorizedUser = userDataService.findByIdAndStatus(userId, UserStatus.ACTIVE)
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

    public void expelMember(Long userId, Long circleId, Long memberId) {

        //임원들 가능
        User authorizedUser = userDataService.findByIdAndStatus(userId, UserStatus.ACTIVE)
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
}
