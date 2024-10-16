package com.circleon.authentication.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Column(nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private LocalDateTime lastAttemptTime;

    @Column(nullable = false)
    private boolean isVerified;

    @Builder
    public EmailVerification(String email, String verificationCode, LocalDateTime expirationTime, int attemptCount, LocalDateTime lastAttemptTime) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expirationTime = expirationTime;
        this.attemptCount = attemptCount;
        this.lastAttemptTime = lastAttemptTime;
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public void resetAttemptCount() {
        this.attemptCount = 0;
    }

}
