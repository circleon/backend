package com.circleon.domain.circle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CircleLeaveMessage {

    private String leaveMessage;

    public static CircleLeaveMessage of(String leaveMessage) {
        return CircleLeaveMessage.builder()
                .leaveMessage(leaveMessage)
                .build();
    }
}
