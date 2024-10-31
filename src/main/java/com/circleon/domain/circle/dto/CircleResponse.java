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
public class CircleResponse {

    private Long circleId;

    private String profileImgUrl;

    private String thumbnailUrl;

    private CategoryType category;

    private LocalDateTime createdAt;

    private int memberCount;

    public static CircleResponse fromCircle(Circle circle, int memberCount) {
        return CircleResponse.builder()
                .circleId(circle.getId())
                .category(circle.getCategoryType())
                .profileImgUrl(circle.getProfileImgUrl())
                .thumbnailUrl(circle.getThumbnailUrl())
                .createdAt(circle.getCreatedAt())
                .memberCount(memberCount)
                .build();
    }
}
