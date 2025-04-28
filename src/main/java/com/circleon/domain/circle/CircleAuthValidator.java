package com.circleon.domain.circle;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.repository.MyCircleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CircleAuthValidator {

    private final MyCircleRepository myCircleRepository;

    public MyCircle validateExecutiveAccess(Long userId, Long circleId, String errorMessage) {


        MyCircle member = myCircleRepository.findJoinedMember(userId, circleId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND, errorMessage));

        if(member.getCircleRole() == CircleRole.MEMBER){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "동아리 임원이 아닙니다.");
        }

        return member;
    }

    public MyCircle validatePresidentAccess(Long userId, Long circleId) {

        MyCircle member = myCircleRepository.findJoinedMember(userId, circleId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBER_NOT_FOUND, "[validatePresidentAccess] 멤버가 아닙니다."));

        if (member.getCircleRole() != CircleRole.PRESIDENT) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS);
        }
        return member;
    }

    public MyCircle validateMembership(Long userId, Long circleId) {
        return myCircleRepository.findJoinedMember(userId, circleId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));
    }
}
