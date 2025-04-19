package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.OfficialStatus;
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

    private String circleName;

    private String profileImgUrl;

    private String thumbnailUrl;

    private CategoryType category;

    private LocalDateTime createdAt;

    private String summary;

    private int memberCount;

    private OfficialStatus officialStatus;

    public static CircleResponse fromCircle(Circle circle) {
        return CircleResponse.builder()
                .circleId(circle.getId())
                .circleName(circle.getName())
                .category(circle.getCategoryType())
                .profileImgUrl(circle.getProfileImgUrl())
                .thumbnailUrl(circle.getThumbnailUrl())
                .createdAt(circle.getCreatedAt())
                .memberCount(circle.getMemberCount())
                .summary(circle.getSummary())
                .officialStatus(circle.getOfficialStatus())
                .build();
    }
}
