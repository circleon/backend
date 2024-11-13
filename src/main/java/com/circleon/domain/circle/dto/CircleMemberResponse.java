package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.MyCircle;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleMemberResponse {

    private Long memberId;

    private String memberName;

    private CircleRole circleRole;

    private LocalDateTime joinedAt;

    private MembershipStatus membershipStatus;

    public static CircleMemberResponse fromMyCircle(MyCircle myCircle){
        return CircleMemberResponse.builder()
                .memberId(myCircle.getId())
                .memberName(myCircle.getUser().getUsername())
                .circleRole(myCircle.getCircleRole())
                .joinedAt(myCircle.getJoinedAt())
                .membershipStatus(myCircle.getMembershipStatus())
                .build();
    }
}
