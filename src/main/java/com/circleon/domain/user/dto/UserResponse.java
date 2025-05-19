package com.circleon.domain.user.dto;


import com.circleon.domain.user.entity.UnivCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private UnivCode univCode;

    private String profileImgUrl;

    public static UserResponse from(UserDomain userDomain){
        return UserResponse.builder()
                .id(userDomain.getId())
                .username(userDomain.getUsername())
                .email(userDomain.getEmail())
                .univCode(userDomain.getUnivCode())
                .profileImgUrl(userDomain.getProfileImgUrl())
                .build();
    }
}
