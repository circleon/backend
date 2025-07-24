package com.circleon.authentication.service;

import com.circleon.authentication.AuthConstants;
import com.circleon.authentication.AuthMailProperties;
import com.circleon.authentication.dto.SignUpRequest;
import com.circleon.authentication.email.dto.AwsSesEmailRequest;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;
import com.circleon.authentication.email.service.EmailService;
import com.circleon.authentication.entity.EmailVerification;
import com.circleon.authentication.repository.EmailVerificationRepository;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserPolicyAgreementRepository;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthSignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final UserPolicyAgreementRepository userPolicyAgreementRepository;
    private final AuthMailProperties authMailProperties;

    @Transactional
    public void registerUser(SignUpRequest signUpRequest) {

        // 이메일 중복 체크
        if(userRepository.existsByEmailAndStatus(signUpRequest.getEmail(), UserStatus.ACTIVE)) {
            throw new UserException(UserResponseStatus.EMAIL_DUPLICATE);
        }

        // 허용된 이메일인지 한번 더 체크
        String email = signUpRequest.getEmail();
        UnivCode foundUnivCode = findMatchingUnivCodeByEmail(email);

        //이메일 인증 여부 확인
        EmailVerification emailVerification = getEmailVerification(email);

        //저장 (비밀번호 암호화)
        String encryptedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        User user = User.builder()
                .email(email)
                .password(encryptedPassword)
                .username(signUpRequest.getUsername())
                .univCode(foundUnivCode)
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        userPolicyAgreementRepository.save(signUpRequest.toUserPolicyAgreement(user.getId()));
        emailVerificationRepository.delete(emailVerification);
    }

    private EmailVerification getEmailVerification(String email) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserResponseStatus.VERIFICATION_CODE_NOT_REQUESTED));

        if(!emailVerification.isVerified()){
            throw new UserException(UserResponseStatus.VERIFICATION_CODE_NOT_REQUESTED);
        }

        return emailVerification;
    }

    private UnivCode findMatchingUnivCodeByEmail(String email) {
        return Arrays.stream(UnivCode.values())
                .filter(univCode -> email.endsWith("@" + univCode.getEmail()))
                .findFirst()
                .orElseThrow(() -> new UserException(UserResponseStatus.INVALID_UNIV_EMAIL_DOMAIN));
    }

    public CompletableFuture<Void> sendAsyncVerificationEmail(EmailVerificationRequest emailVerificationRequest) {

        if(userRepository.existsByEmailAndStatus(emailVerificationRequest.getEmail(), UserStatus.ACTIVE)) {
            throw new UserException(UserResponseStatus.EMAIL_DUPLICATE);
        }

        String verificationCode = emailService.generateVerificationCode();
        String encryptedCode = passwordEncoder.encode(verificationCode);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime codeExpirationTime = LocalDateTime.now().plusMinutes(AuthConstants.EXPIRATION_TIME);

        EmailVerification emailVerification = emailVerificationRepository
                .findByEmail(emailVerificationRequest.getEmail())
                .orElseGet(()->EmailVerification
                        .of(emailVerificationRequest.getEmail(), encryptedCode, codeExpirationTime, now)
                );

        if(emailVerification.isAttemptLimitReached(AuthConstants.ATTEMPT_THRESHOLD,
                now.minusMinutes(AuthConstants.ATTEMPT_THRESHOLD_MINUTES))){
            throw new UserException(UserResponseStatus.TOO_MANY_ATTEMPTS);
        }

        AwsSesEmailRequest awsSesEmailRequest =
                createAwsEmailRequest(verificationCode, emailVerificationRequest.getEmail());

        return emailService.sendAsyncEmail(awsSesEmailRequest)
                .thenAccept(result->{
                    emailVerification.startNewAttempt(encryptedCode, now, codeExpirationTime);
                    emailVerificationRepository.save(emailVerification);
                })
                .exceptionally(throwable -> {
                    throw new UserException(UserResponseStatus.EMAIL_SERVICE_UNAVAILABLE, "[sendAsyncEmail] 이메일 전송 서비스 이용불가");
                });
    }

    private AwsSesEmailRequest createAwsEmailRequest(String verificationCode, String recipientEmail) {
        return AwsSesEmailRequest.builder()
                .sender(authMailProperties.getSourceMail())
                .subject("[Circle On] 인증 코드")
                .content(verificationCode)
                .recipient(recipientEmail)
                .build();
    }

    @Transactional
    public void verifyVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(verificationCodeRequest.getEmail())
                .orElseThrow(() -> new UserException(UserResponseStatus.VERIFICATION_CODE_NOT_REQUESTED));

        if(!emailVerification.isCodeValid(LocalDateTime.now())) {
            throw new UserException(UserResponseStatus.VERIFICATION_CODE_EXPIRED);
        }

        if(!passwordEncoder.matches(verificationCodeRequest.getCode(), emailVerification.getVerificationCode())){
            throw new UserException(UserResponseStatus.INVALID_VERIFICATION_CODE);
        }
        emailVerification.verify();
    }
}
