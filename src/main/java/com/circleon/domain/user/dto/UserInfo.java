package com.circleon.domain.user.dto;

import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserInfo {

    private Long id;

    private String username;

    private String email;

    private UnivCode univCode;

    private UserStatus status;

    private String profileImgUrl;

    public void changeImgUrlToSignedUrl(String signedUrl) {
        this.profileImgUrl = signedUrl;
    }

    public static UserInfo from(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .univCode(user.getUnivCode())
                .status(user.getStatus())
                .profileImgUrl(user.getProfileImgUrl())
                .build();
    }

}
