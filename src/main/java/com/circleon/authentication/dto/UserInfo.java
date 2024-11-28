package com.circleon.authentication.dto;

import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private Long userId;

    private String username;

    private UnivCode univCode;

    public static UserInfo from(User user) {
        return UserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .univCode(user.getUnivCode())
                .build();
    }
}
