package com.circleon.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(nullable = false)
    private UnivCode univCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Role role;

    @Column
    private String profileImgUrl;

    @PrePersist
    public void prePersist(){
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }

}
