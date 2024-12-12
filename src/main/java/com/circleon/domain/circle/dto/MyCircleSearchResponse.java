package com.circleon.domain.circle.dto;


import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.MembershipStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCircleSearchResponse {

    private Long memberId;

    private LocalDateTime joinedAt;

    private MembershipStatus membershipStatus;

    private CircleRole circleRole;

    private CircleInfo circleInfo;
}
