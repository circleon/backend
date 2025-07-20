package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.MyCircle;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCircleCreateResponse {

    private Long memberId;

    private MembershipStatus membershipStatus;

    public static MyCircleCreateResponse fromMyCircle(MyCircle myCircle) {
        return MyCircleCreateResponse.builder()
                .memberId(myCircle.getId())
                .membershipStatus(myCircle.getMembershipStatus())
                .build();
    }
}
