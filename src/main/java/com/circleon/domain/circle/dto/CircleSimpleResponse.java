package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.entity.Circle;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleSimpleResponse {

    private Long circleId;

    private String circleName;

    private CategoryType categoryType;

    public static CircleSimpleResponse fromCircle(Circle circle) {
        return CircleSimpleResponse.builder()
                .circleId(circle.getId())
                .circleName(circle.getName())
                .categoryType(circle.getCategoryType())
                .build();
    }
}
