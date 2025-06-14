package com.circleon.domain.user.entity;

import com.circleon.common.BaseEntity;
import com.circleon.domain.user.dto.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Entity
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private UnivCode univCode;

    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Role role;

    @Column
    private String profileImgUrl;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateUserName(String username){
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        this.username = username;
    }

    public void updateProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }

    public void withdraw(){
        if(Objects.isNull(id)){
            throw new IllegalStateException("회원 탈퇴는 저장된 사용자에게만 적용할 수 있습니다.");
        }
        status = UserStatus.DEACTIVATED;
        email = email + "#DEACTIVATED#" + id;
    }

}
