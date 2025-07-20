package com.circleon.domain.schedule.circle.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleMonthlySchedules {

    private int year;

    private int month;

    private CircleInfo circleInfo;

    private List<CircleScheduleDetail> schedules;

    public static CircleMonthlySchedules of(int year, int month, CircleInfo circleInfo, List<CircleScheduleDetail> schedules) {
        return CircleMonthlySchedules.builder()
                .year(year)
                .month(month)
                .circleInfo(circleInfo)
                .schedules(schedules)
                .build();
    }
}
