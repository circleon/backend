package com.circleon.domain.schedule.circle.dto;

import com.circleon.domain.schedule.circle.entity.CircleSchedule;
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

    public static CircleScheduleDetail fromCircleSchedule(CircleSchedule circleSchedule) {
        return CircleScheduleDetail.builder()
                .circleScheduleId(circleSchedule.getId())
                .title(circleSchedule.getTitle())
                .content(circleSchedule.getContent())
                .startAt(circleSchedule.getStartAt())
                .endAt(circleSchedule.getEndAt())
                .build();
    }
}
