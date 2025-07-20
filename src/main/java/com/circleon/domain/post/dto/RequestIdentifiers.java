package com.circleon.domain.post.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestIdentifiers {

    private Long userId;

    private Long circleId;

    private Long postId;

    public static RequestIdentifiers of(Long userId, Long circleId, Long postId) {
        return RequestIdentifiers.builder()
                .userId(userId)
                .circleId(circleId)
                .postId(postId)
                .build();
    }
}
