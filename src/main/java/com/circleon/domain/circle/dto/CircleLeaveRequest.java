package com.circleon.domain.circle.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleLeaveRequest {

    @Size(max = 255, message = "최대 255자까지 입력할 수 있습니다.")
    private String leaveMessage;
}
