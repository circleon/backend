package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.user.entity.UserStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyCircleSearchCondition {

    private Long userId;

    private Long circleId;

    private UserStatus userStatus;

    private CircleStatus circleStatus;

    private MembershipStatus membershipStatus;

}
