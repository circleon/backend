package com.circleon.domain.user.entity;

import com.circleon.common.BaseEntity;
import com.circleon.domain.user.dto.UserDomain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    public UserDomain toDomain(){
        return UserDomain.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .univCode(univCode)
                .status(status)
                .role(role)
                .profileImgUrl(profileImgUrl)
                .build();
    }

}
