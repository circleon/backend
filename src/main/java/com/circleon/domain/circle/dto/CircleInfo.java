package com.circleon.domain.circle.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleInfo {

    private Long circleId;

    private String circleName;

    private String thumbnailUrl;
}
