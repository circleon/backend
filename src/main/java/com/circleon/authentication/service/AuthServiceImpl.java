package com.circleon.authentication.service;

import com.circleon.authentication.AuthConstants;
import com.circleon.authentication.dto.*;
import com.circleon.authentication.email.dto.AwsSesEmailRequest;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;
import com.circleon.authentication.email.service.EmailService;
import com.circleon.authentication.entity.EmailVerification;
import com.circleon.authentication.entity.RefreshToken;
import com.circleon.authentication.jwt.JwtUtil;
import com.circleon.authentication.repository.EmailVerificationRepository;
import com.circleon.authentication.repository.RefreshTokenRepository;
import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService{


    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Override
    public void registerUser(SignUpRequest signUpRequest) {

        // 이메일 중복 체크
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
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
                .userStatus(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

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

    //TODO 비동기 처리로 바꿔야함 성능때문에
    @Override
    public void sendVerificationEmail(EmailVerificationRequest emailVerificationRequest) {

        // 먼저 이메일 중복 여부 체크
        if(userRepository.existsByEmail(emailVerificationRequest.getEmail())) {

            throw new UserException(UserResponseStatus.EMAIL_DUPLICATE);
        }

        // 이메일 인증 테이블에서 이메일 존재 여부 확인
        EmailVerification existingVerification = emailVerificationRepository
                .findByEmail(emailVerificationRequest.getEmail())
                .orElse(null);

        //코드 생성
        String verificationCode = emailService.generateVerificationCode();
        String encryptedCode = passwordEncoder.encode(verificationCode);

        if(existingVerification != null) { //존재

            handleExistingVerification(existingVerification, encryptedCode);
        }else{
            //인증 코드 저장
            createNewVerification(emailVerificationRequest, encryptedCode);
        }


        //TODO 인증 메시지 포멧 다시 정하기
        AwsSesEmailRequest awsSesEmailRequest = AwsSesEmailRequest.builder()
                .sender(AuthConstants.SOURCE_MAIL)
                .subject("[Circle On] 인증 코드")
                .content(verificationCode)
                .recipient(emailVerificationRequest.getEmail())
                .build();

        emailService.sendEmail(awsSesEmailRequest);
    }

    @Override
    public void verifyVerificationCode(VerificationCodeRequest verificationCodeRequest) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(verificationCodeRequest.getEmail())
                .orElseThrow(() -> new UserException(UserResponseStatus.VERIFICATION_CODE_NOT_REQUESTED));

        if(emailVerification.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new UserException(UserResponseStatus.VERIFICATION_CODE_EXPIRED);
        }

        if(!passwordEncoder.matches(verificationCodeRequest.getCode(), emailVerification.getVerificationCode())){
            throw new UserException(UserResponseStatus.INVALID_VERIFICATION_CODE);
        }

        emailVerification.setVerified(true);
    }

    // 새로운 인증 정보 생성
    private void createNewVerification(EmailVerificationRequest emailVerificationRequest, String verificationCode) {
        EmailVerification emailVerification = EmailVerification.builder()
                .email(emailVerificationRequest.getEmail())
                .verificationCode(verificationCode)
                .expirationTime(LocalDateTime.now().plusMinutes(AuthConstants.EXPIRATION_TIME))
                .attemptCount(1)
                .isVerified(false)
                .lastAttemptTime(LocalDateTime.now())
                .build();

        emailVerificationRepository.save(emailVerification);
    }

    // 기존 인증 정보 처리
    private void handleExistingVerification(EmailVerification existingVerification, String verificationCode) {
        //하루 지나면 초기화
        if(existingVerification.getLastAttemptTime().isBefore(LocalDateTime.now().minusDays(1))) {
            existingVerification.resetAttemptCount();
        }

        if(existingVerification.getExpirationTime().isAfter(LocalDateTime.now())){
            //아직 인증 코드가 유효
            throw new UserException(UserResponseStatus.VERIFICATION_CODE_NOT_EXPIRED);
        }

        if(existingVerification.getAttemptCount() >= AuthConstants.ATTEMPT_THRESHOLD){
            // 회수 제한
            throw new UserException(UserResponseStatus.TOO_MANY_ATTEMPTS);
        }

        // 시도 횟수 증가 및 인증 코드 갱신
        existingVerification.incrementAttemptCount();
        existingVerification.setExpirationTime(LocalDateTime.now().plusMinutes(AuthConstants.EXPIRATION_TIME));
        existingVerification.setLastAttemptTime(LocalDateTime.now());
        existingVerification.setVerificationCode(verificationCode);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User foundUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserException(UserResponseStatus.EMAIL_NOT_FOUND));

        //비밀번호 검증
        if(!passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())){
            throw new UserException(UserResponseStatus.PASSWORD_MISMATCH);
        }

        //token 생성
        String newAccessToken = jwtUtil.createAccessToken(foundUser.getId(), foundUser.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(foundUser.getId(), foundUser.getRole().name());

        Date expiration = jwtUtil.getExpiration(newRefreshToken);

        LocalDateTime expiresAt = getLocalDateTime(expiration);

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(newRefreshToken)
                .accessToken(newAccessToken)
                .expiresAt(expiresAt)
                .user(foundUser)
                .build();

        refreshTokenRepository.save(refreshToken);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(foundUser.getId())
                .build();
    }

    private LocalDateTime getLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.getRefreshToken();

        //리프레쉬 존재 여부 (로그아웃 됐는지)
        RefreshToken foundRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CommonException(CommonResponseStatus.REFRESH_TOKEN_INVALID));

        //리프레쉬 토큰 검증 and 리프레쉬 토큰에 해당하는 액세스 토큰 만료 여부 확인 -> 만료 아니면 로그아웃 처리
        if(isInvalidRefreshOrStillValidAccessToken(foundRefreshToken)){
            refreshTokenRepository.delete(foundRefreshToken);
            return null;
        }

        //새로운 액세스 토큰
        try{
            Long userId = jwtUtil.getUserId(foundRefreshToken.getRefreshToken());
            String role = jwtUtil.getRole(foundRefreshToken.getRefreshToken());

            String accessToken = jwtUtil.createAccessToken(userId, role);

            foundRefreshToken.setAccessToken(accessToken);

            return RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .build();
        }catch (Exception e){
            refreshTokenRepository.delete(foundRefreshToken);
            return null;
        }

    }

    private boolean isInvalidRefreshOrStillValidAccessToken(RefreshToken foundRefreshToken) {
        return !jwtUtil.validateToken(foundRefreshToken.getRefreshToken())
                || jwtUtil.validateToken(foundRefreshToken.getAccessToken());
    }

    //TODO 좀 더 고민
    @Override
    public void logout(LogoutRequest logoutRequest) {
        refreshTokenRepository.deleteByRefreshToken(logoutRequest.getRefreshToken());
    }
}
