package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.user.entity.User;
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

    private String memberImgUrl;

    private CircleRole circleRole;

    private LocalDateTime joinedAt;

    private MembershipStatus membershipStatus;

    public static CircleMemberResponse fromMyCircle(MyCircle myCircle){
        User user = myCircle.getUser();
        String memberName = user.isActive() ? user.getUsername() : "알수없음";
        String memberImgUrl = user.isActive() ? user.getProfileImgUrl() : null;
        return CircleMemberResponse.builder()
                .memberId(myCircle.getId())
                .memberName(memberName)
                .memberImgUrl(memberImgUrl)
                .circleRole(myCircle.getCircleRole())
                .joinedAt(myCircle.getJoinedAt())
                .membershipStatus(myCircle.getMembershipStatus())
                .build();
    }
}
