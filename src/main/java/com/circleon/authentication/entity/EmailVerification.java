package com.circleon.authentication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
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

    public static EmailVerification of(String email,
                                       String verificationCode,
                                       LocalDateTime expirationTime,
                                       LocalDateTime now) {
        return EmailVerification.builder()
                .email(email)
                .verificationCode(verificationCode)
                .expirationTime(expirationTime)
                .lastAttemptTime(now)
                .attemptCount(0)
                .isVerified(false)
                .build();
    }

    public boolean isAttemptLimitReached(int countLimit, LocalDateTime resetThresholdTime) {
        if(lastAttemptTime.isBefore(resetThresholdTime)) {
            resetAttemptCount();
        }
        return attemptCount >= countLimit;
    }

    public void startNewAttempt(String verificationCode, LocalDateTime now, LocalDateTime codeExpirationTime) {
        this.verificationCode = verificationCode;
        this.lastAttemptTime = now;
        this.expirationTime = codeExpirationTime;
        attemptCount++;
        isVerified = false;
    }

    public boolean isCodeValid(LocalDateTime now) {
        return expirationTime.isAfter(now);
    }

    public void verify(){
        isVerified = true;
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public void resetAttemptCount() {
        this.attemptCount = 0;
    }

}
