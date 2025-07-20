package com.circleon.domain.circle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CircleJoinRequest {

    @NotBlank
    private String joinMessage;
}
