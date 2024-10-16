package com.circleon.authentication.entity;

import com.circleon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(indexes = @Index(columnList = "refresh_token"))
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Version
    private Long version;

    @Builder
    public RefreshToken(String refreshToken, String accessToken, LocalDateTime expiresAt, User user) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
        this.user = user;
    }
}
