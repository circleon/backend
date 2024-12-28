package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CircleRole;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleRoleUpdateRequest {

    @NotBlank
    private CircleRole circleRole;
}
