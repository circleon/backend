package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.MembershipStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipStatusUpdateRequest {

    private MembershipStatus membershipStatus;
}
