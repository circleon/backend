package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.entity.Circle;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleInfoUpdateResponse {

    private Long circleId;

    private String circleName;

    private CategoryType category;

    private String introduction;

    private String summary;

    private LocalDateTime recruitmentStartDate;

    private LocalDateTime recruitmentEndDate;

    public static CircleInfoUpdateResponse fromCircle(Circle circle) {
        return CircleInfoUpdateResponse.builder()
                .circleId(circle.getId())
                .circleName(circle.getName())
                .category(circle.getCategoryType())
                .introduction(circle.getIntroduction())
                .recruitmentStartDate(circle.getRecruitmentStartDate())
                .recruitmentEndDate(circle.getRecruitmentEndDate())
                .summary(circle.getSummary())
                .build();
    }
}
