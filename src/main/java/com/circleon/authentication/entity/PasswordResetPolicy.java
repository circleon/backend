package com.circleon.authentication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PasswordResetPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false)
    private LocalDateTime codeExpirationAt;

    @Column(nullable = false)
    private int attemptCount;

    @Column(nullable = false)
    private LocalDateTime lastAttemptAt;

    @Column(nullable = false)
    private String verificationCode;

    @Column(nullable = false)
    private boolean verified;

    public boolean isAttemptLimitReached(int countLimit, LocalDateTime codeExpirationAt) {
        if(lastAttemptAt.isBefore(codeExpirationAt)){
            resetAttemptCount();
        }
        return attemptCount >= countLimit;
    }

    public void startNewAttempt(String verificationCode, LocalDateTime now, LocalDateTime codeExpirationAt){
        this.verificationCode = verificationCode;
        this.lastAttemptAt = now;
        this.codeExpirationAt = codeExpirationAt;
        this.publicId = UUID.randomUUID().toString();
        attemptCount++;
        verified = false;
    }

    private void resetAttemptCount() {
        this.attemptCount = 0;
    }

    public boolean isCodeValid(LocalDateTime now){
        return codeExpirationAt.isAfter(now);
    }

    public void verify(){
        verified = true;
    }

    public static PasswordResetPolicy of(String verificationCode, Long userId, LocalDateTime now, LocalDateTime codeExpirationAt) {
        return PasswordResetPolicy.builder()
                .userId(userId)
                .publicId(UUID.randomUUID().toString())
                .verificationCode(verificationCode)
                .lastAttemptAt(now)
                .codeExpirationAt(codeExpirationAt)
                .attemptCount(0)
                .verified(false)
                .build();
    }
}
