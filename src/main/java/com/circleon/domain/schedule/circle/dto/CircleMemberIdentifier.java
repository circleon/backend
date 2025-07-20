package com.circleon.domain.schedule.circle.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleMemberIdentifier {

    private Long userId;

    private Long circleId;

    public static CircleMemberIdentifier of(Long userId, Long circleId) {
        return CircleMemberIdentifier.builder()
                .userId(userId)
                .circleId(circleId)
                .build();
    }
}
