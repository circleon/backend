package com.circleon.domain.circle.dto;


import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.MembershipStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCircleSearchResponse {

    private Long circleId;

    private String circleName;

    private String thumbnailUrl;

    private CategoryType categoryType;

}
