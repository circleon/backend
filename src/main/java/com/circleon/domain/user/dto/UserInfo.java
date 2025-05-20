package com.circleon.domain.user.dto;

import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.UnivCode;
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

    private String password;

    private UnivCode univCode;

    private UserStatus status;

    private Role role;

    private String profileImgUrl;

    public void updateUserName(String username){
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        this.username = username;
    }

    public void updateProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }

}
