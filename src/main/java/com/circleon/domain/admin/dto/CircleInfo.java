package com.circleon.domain.admin.dto;

import com.circleon.domain.circle.entity.Circle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CircleInfo {

    private Long circleId;

    private String circleName;

    private String summary;

    private String introduction;

    public static CircleInfo from(Circle circle) {
        return CircleInfo.builder()
                .circleId(circle.getId())
                .circleName(circle.getName())
                .summary(circle.getSummary())
                .introduction(circle.getIntroduction())
                .build();
    }

    public static CircleInfo empty() {
        return CircleInfo.builder()
                .circleId(null)
                .circleName("알 수 없음")
                .summary("")
                .introduction("")
                .build();
    }
}
