package com.circleon.authentication.service;

import com.circleon.authentication.AuthConstants;
import com.circleon.authentication.AuthMailProperties;
import com.circleon.authentication.dto.PasswordResetCodeVerificationRequest;
import com.circleon.authentication.dto.PasswordResetCodeVerificationResponse;
import com.circleon.authentication.dto.PasswordResetPolicyResponse;
import com.circleon.authentication.dto.PasswordResetRequest;
import com.circleon.authentication.email.dto.AwsSesEmailRequest;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.service.EmailService;
import com.circleon.authentication.entity.PasswordResetPolicy;
import com.circleon.authentication.repository.PasswordResetPolicyRepository;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetPolicyRepository passwordResetPolicyRepository;
    private final AuthMailProperties authMailProperties;

    public CompletableFuture<PasswordResetPolicyResponse> sendAsyncVerificationCodeForPasswordReset(EmailVerificationRequest emailVerificationRequest) {

        User user = userRepository.findByEmailAndStatus(emailVerificationRequest.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND, "존재하지 않는 유저입니다"));

        String verificationCode = emailService.generateVerificationCode();
        String encryptedCode = passwordEncoder.encode(verificationCode);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime codeExpirationTime = LocalDateTime.now().plusMinutes(AuthConstants.EXPIRATION_TIME);

        PasswordResetPolicy passwordResetPolicy = passwordResetPolicyRepository.findByUserId(user.getId())
                .orElseGet(() -> PasswordResetPolicy.of(encryptedCode, user.getId(), now, codeExpirationTime));

        if(passwordResetPolicy.isAttemptLimitReached(AuthConstants.ATTEMPT_THRESHOLD,
                now.minusMinutes(AuthConstants.ATTEMPT_THRESHOLD_MINUTES))){
            throw new UserException(UserResponseStatus.TOO_MANY_ATTEMPTS);
        }

        AwsSesEmailRequest awsSesEmailRequest = createAwsEmailRequest(verificationCode, user.getEmail());

        return emailService.sendAsyncEmail(awsSesEmailRequest)
                .thenApply(result->{
                    passwordResetPolicy.startNewAttempt(encryptedCode, now, codeExpirationTime);
                    PasswordResetPolicy savedPolicy = passwordResetPolicyRepository.save(passwordResetPolicy);
                    return PasswordResetPolicyResponse.of(savedPolicy.getId());
                })
                .exceptionally(throwable -> {
                    throw new UserException(UserResponseStatus.EMAIL_SERVICE_UNAVAILABLE, "[sendAsyncEmail] 이메일 전송 서비스 이용불가");
                });
    }

    //TODO 인증 메시지 포멧 다시 정하기
    private AwsSesEmailRequest createAwsEmailRequest(String verificationCode, String recipientEmail) {
        return AwsSesEmailRequest.builder()
                .sender(authMailProperties.getSourceMail())
                .subject("[Circle On] 인증 코드")
                .content(verificationCode)
                .recipient(recipientEmail)
                .build();
    }

    @Transactional
    public PasswordResetCodeVerificationResponse verifyCodeForPasswordReset(PasswordResetCodeVerificationRequest codeVerificationRequest) {

        PasswordResetPolicy policy = passwordResetPolicyRepository.findById(codeVerificationRequest.policyId())
                .orElseThrow(() -> new UserException(UserResponseStatus.VERIFICATION_CODE_NOT_REQUESTED));

        if(!policy.isCodeValid(LocalDateTime.now())){
            throw new UserException(UserResponseStatus.VERIFICATION_CODE_EXPIRED);
        }

        if(!passwordEncoder.matches(codeVerificationRequest.code(), policy.getVerificationCode())){
            throw new UserException(UserResponseStatus.INVALID_VERIFICATION_CODE);
        }

        policy.verify();
        return PasswordResetCodeVerificationResponse.of(policy.getPublicId());
    }

    @Transactional
    public void updatePassword(PasswordResetRequest passwordResetRequest) {

        PasswordResetPolicy policy = passwordResetPolicyRepository.findByPublicId(passwordResetRequest.publicId())
                .orElseThrow(() -> new UserException(UserResponseStatus.VERIFICATION_CODE_NOT_REQUESTED));

        if(!policy.isVerified()){
            throw new UserException(UserResponseStatus.INVALID_VERIFICATION_CODE);
        }

        if(!policy.isCodeValid(LocalDateTime.now())){
            throw new UserException(UserResponseStatus.VERIFICATION_CODE_EXPIRED);
        }

        User user = userRepository.findByIdAndStatus(policy.getUserId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        user.updatePassword(passwordEncoder.encode(passwordResetRequest.newPassword()));

        passwordResetPolicyRepository.delete(policy);
    }
}
