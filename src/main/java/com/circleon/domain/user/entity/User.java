package com.circleon.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnivCode univCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String profileImgUrl;

    @Builder
    public User(Long id, String username, String email, String password, UnivCode univCode, UserStatus status, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.univCode = univCode;
        this.status = status;
        this.role = role;
    }

    @PrePersist
    public void prePersist(){
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }

}
