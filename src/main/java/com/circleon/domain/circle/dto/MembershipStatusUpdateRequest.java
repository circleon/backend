package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.MembershipStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipStatusUpdateRequest {

    @NotBlank
    private MembershipStatus membershipStatus;
}
