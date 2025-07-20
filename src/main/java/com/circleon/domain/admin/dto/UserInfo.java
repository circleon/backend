package com.circleon.domain.admin.dto;

import com.circleon.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfo {

    private Long userId;

    private String username;

    public static UserInfo from(User user) {
        return UserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }
}
