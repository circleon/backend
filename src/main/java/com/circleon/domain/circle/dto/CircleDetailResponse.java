package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.entity.Circle;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleDetailResponse {

    private Long circleId;

    private String circleName;

    private String profileImgUrl;

    private String thumbnailUrl;

    private String introImgUrl;

    private CategoryType category;

    private LocalDateTime createdAt;

    private String introduction;

    private LocalDateTime recruitmentStartDate;

    private LocalDateTime recruitmentEndDate;

    private String summary;

    private MembershipStatus membershipStatus;

    private CircleRole circleRole;

    private Long memberId;

    private int memberCount;

    private OfficialStatus officialStatus;

    private boolean recruiting;

    public static CircleDetailResponse fromCircle(Circle circle, MembershipStatus membershipStatus, CircleRole circleRole, Long memberId) {
        return CircleDetailResponse.builder()
                .circleId(circle.getId())
                .circleName(circle.getName())
                .category(circle.getCategoryType())
                .profileImgUrl(circle.getProfileImgUrl())
                .thumbnailUrl(circle.getThumbnailUrl())
                .introImgUrl(circle.getIntroImgUrl())
                .introduction(circle.getIntroduction())
                .summary(circle.getSummary())
                .recruitmentStartDate(circle.getRecruitmentStartDate())
                .recruitmentEndDate(circle.getRecruitmentEndDate())
                .createdAt(circle.getCreatedAt())
                .memberCount(circle.getMemberCount())
                .membershipStatus(membershipStatus)
                .circleRole(circleRole)
                .memberId(memberId)
                .officialStatus(circle.getOfficialStatus())
                .recruiting(circle.isRecruiting())
                .build();
    }
}
