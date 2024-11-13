package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CircleRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleRoleUpdateRequest {

    private CircleRole circleRole;
}
