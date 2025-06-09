package com.circleon.domain.user.dto;


import com.circleon.domain.user.entity.UnivCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserResponse {

    private Long userId;

    private String username;

    private String email;

    private UnivCode univCode;

    private String profileImgUrl;

    public static UserResponse from(UserInfo userInfo){
        return UserResponse.builder()
                .userId(userInfo.getId())
                .username(userInfo.getUsername())
                .email(userInfo.getEmail())
                .univCode(userInfo.getUnivCode())
                .profileImgUrl(userInfo.getProfileImgUrl())
                .build();
    }
}
