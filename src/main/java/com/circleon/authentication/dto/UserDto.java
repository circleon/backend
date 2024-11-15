package com.circleon.authentication.dto;

import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long userId;

    private String username;

    private UnivCode univCode;

    public static UserDto from(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .univCode(user.getUnivCode())
                .build();
    }
}
