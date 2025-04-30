package com.circleon.domain.admin.dto;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.entity.Circle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CircleResponse {

    private Long id;

    private String name;

    private String presidentEmail;

    private String presidentName;

    private String profileImgUrl;

    private String thumbnailUrl;

    private String introImgUrl;

    private CircleStatus circleStatus;

    private CategoryType categoryType;

    private String introduction;

    private String summary;

    private OfficialStatus officialStatus;

    public static CircleResponse from(Circle circle) {
        return CircleResponse.builder()
                .id(circle.getId())
                .name(circle.getName())
                .presidentEmail(circle.getApplicant().getEmail())
                .presidentName(circle.getApplicant().getUsername())
                .profileImgUrl(circle.getProfileImgUrl())
                .thumbnailUrl(circle.getThumbnailUrl())
                .introImgUrl(circle.getIntroImgUrl())
                .circleStatus(circle.getCircleStatus())
                .categoryType(circle.getCategoryType())
                .introduction(circle.getIntroduction())
                .summary(circle.getSummary())
                .officialStatus(circle.getOfficialStatus())
                .build();
    }
}
