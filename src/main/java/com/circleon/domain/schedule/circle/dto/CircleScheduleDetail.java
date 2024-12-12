package com.circleon.domain.schedule.circle.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleScheduleDetail {

    private Long circleScheduleId;

    private String title;

    private String content;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
