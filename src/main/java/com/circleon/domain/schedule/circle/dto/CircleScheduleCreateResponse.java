package com.circleon.domain.schedule.circle.dto;

import com.circleon.domain.schedule.circle.entity.CircleSchedule;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleScheduleCreateResponse {

    private Long circleScheduleId;

    private String title;

    private String content;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    public static CircleScheduleCreateResponse fromCircleSchedule(CircleSchedule circleSchedule) {
        return CircleScheduleCreateResponse.builder()
                .circleScheduleId(circleSchedule.getId())
                .title(circleSchedule.getTitle())
                .content(circleSchedule.getContent())
                .startAt(circleSchedule.getStartAt())
                .endAt(circleSchedule.getEndAt())
                .build();
    }
}
