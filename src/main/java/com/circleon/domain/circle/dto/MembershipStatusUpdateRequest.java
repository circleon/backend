package com.circleon.domain.circle.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipStatusUpdateRequest {

    private Boolean approved;

}
