package com.circleon.domain.circle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CircleJoinMessage {

    private String joinMessage;

    public static CircleJoinMessage of(String joinMessage) {
        return CircleJoinMessage.builder()
                .joinMessage(joinMessage).build();
    }
}
