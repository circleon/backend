package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CircleRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleRoleUpdateRequest {

    @NotNull
    private CircleRole circleRole;
}
