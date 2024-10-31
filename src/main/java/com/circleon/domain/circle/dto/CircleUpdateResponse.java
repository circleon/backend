package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleUpdateResponse {

    private Long circleId;

    private String profileImgUrl;

    private String thumbnailUrl;

    private String circleName;

    private CategoryType category;
}
