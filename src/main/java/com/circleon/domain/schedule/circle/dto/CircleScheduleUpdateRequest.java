package com.circleon.domain.schedule.circle.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleScheduleUpdateRequest {

    @Size(max = 255, message = "제목은 최대 255글자까지 입력할 수 있습니다.")
    private String title;

    @Size(max = 1000, message = "일정 내용은 최대 1000글자까지 입력할 수 있습니다.")
    private String content;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
