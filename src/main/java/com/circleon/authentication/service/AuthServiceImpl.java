package com.circleon.authentication.service;

import com.circleon.authentication.AuthConstants;
import com.circleon.authentication.dto.*;
import com.circleon.authentication.email.dto.AwsSesEmailRequest;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;
import com.circleon.authentication.email.service.EmailService;
import com.circleon.authentication.entity.EmailVerification;
import com.circleon.authentication.entity.PasswordResetPolicy;
import com.circleon.authentication.entity.UserRefreshToken;
import com.circleon.authentication.jwt.JwtUtil;
import com.circleon.authentication.repository.EmailVerificationRepository;
import com.circleon.authentication.repository.PasswordResetPolicyRepository;
import com.circleon.authentication.repository.UserRefreshTokenRepository;
import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.UnivCode;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordResetPolicyRepository passwordResetPolicyRepository;

    @Transactional
    @Override
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
    @Transactional
    @Override
    public void sendVerificationEmail(EmailVerificationRequest emailVerificationRequest) {

        // 먼저 이메일 중복 여부 체크
        if(userRepository.existsByEmailAndStatus(emailVerificationRequest.getEmail(), UserStatus.ACTIVE)) {

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


        //TODO 인증 메시지 포멧 다시 정하기 + 보내고 나서 저장하는개 맞을듯?
        AwsSesEmailRequest awsSesEmailRequest = AwsSesEmailRequest.builder()
                .sender(AuthConstants.SOURCE_MAIL)
                .subject("[Circle On] 인증 코드")
                .content(verificationCode)
                .recipient(emailVerificationRequest.getEmail())
                .build();

        emailService.sendEmail(awsSesEmailRequest);
    }


    @Override
    @Transactional
    public CompletableFuture<Void> sendAsyncVerificationEmail(EmailVerificationRequest emailVerificationRequest) {

        // 먼저 이메일 중복 여부 체크
        if(userRepository.existsByEmailAndStatus(emailVerificationRequest.getEmail(), UserStatus.ACTIVE)) {

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

        AwsSesEmailRequest awsSesEmailRequest =
                createAwsEmailRequest(verificationCode, emailVerificationRequest.getEmail());

        return emailService.sendAsyncEmail(awsSesEmailRequest)
                .exceptionally(throwable -> {

                    rollbackResult(emailVerificationRequest.getEmail());

                    throw new UserException(UserResponseStatus.EMAIL_SERVICE_UNAVAILABLE, "[sendAsyncEmail] 이메일 전송 서비스 이용불가");
                });
    }

    private void rollbackResult(String email) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email)
                .orElse(null);

        if(emailVerification != null) {
            int updatedAttemptCount = emailVerification.getAttemptCount() - 1;

            if(updatedAttemptCount > 0){

                emailVerification.setAttemptCount(updatedAttemptCount);
                emailVerification.setExpirationTime(LocalDateTime.now());
                emailVerificationRepository.save(emailVerification);
            }else {
                emailVerificationRepository.delete(emailVerification);
            }
        }
    }

    @Override
    @Transactional
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
        existingVerification.setVerified(false);
    }

    @Transactional
    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User foundUser = userRepository.findByEmailAndStatus(loginRequest.getEmail(), UserStatus.ACTIVE)
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

        UserRefreshToken userRefreshToken = UserRefreshToken.builder()
                .refreshToken(newRefreshToken)
                .accessToken(newAccessToken)
                .expiresAt(expiresAt)
                .user(foundUser)
                .build();

        userRefreshTokenRepository.save(userRefreshToken);

        return LoginResponse.of(UserInfo.from(foundUser), TokenDto.of(newAccessToken, newRefreshToken));
    }

    private LocalDateTime getLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Transactional
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.getRefreshToken();

        //리프레쉬토큰이 검증됐는지도 봐야함
        if(!jwtUtil.validateToken(refreshTokenRequest.getRefreshToken())){
            return null;
        }

        //리프레쉬 존재 여부 (로그아웃 됐는지)
        UserRefreshToken foundUserRefreshToken = userRefreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CommonException(CommonResponseStatus.REFRESH_TOKEN_INVALID));

        //액세스가 아직 유효한데 리프레쉬 요청이 들어오는 경우
        String foundAccessToken = foundUserRefreshToken.getAccessToken();
        if(jwtUtil.validateToken(foundAccessToken)){

            Date now = new Date();
            Date threshold = new Date(now.getTime() - AuthConstants.REFRESH_TOKEN_COMPROMISE_THRESHOLD);
            
            if(jwtUtil.getIssuedAt(foundAccessToken).after(threshold)){
                return RefreshTokenResponse.builder().accessToken(foundAccessToken).build();
            }
            
            //리프레쉬 탈취로 간주
            userRefreshTokenRepository.delete(foundUserRefreshToken);
            return null;
        }

        //새로운 액세스 토큰
        return reissueAccessToken(foundUserRefreshToken);

    }

    private RefreshTokenResponse reissueAccessToken(UserRefreshToken foundUserRefreshToken) {
        try{
            Long userId = jwtUtil.getUserId(foundUserRefreshToken.getRefreshToken());
            String role = jwtUtil.getRole(foundUserRefreshToken.getRefreshToken());

            String accessToken = jwtUtil.createAccessToken(userId, role);

            foundUserRefreshToken.setAccessToken(accessToken);

            return RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .build();
        }catch (Exception e){
            userRefreshTokenRepository.delete(foundUserRefreshToken);
            return null;
        }
    }

    @Override
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
                .sender(AuthConstants.SOURCE_MAIL)
                .subject("[Circle On] 인증 코드")
                .content(verificationCode)
                .recipient(recipientEmail)
                .build();
    }

    @Transactional
    @Override
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
    @Override
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

    //TODO 좀 더 고민
    @Transactional
    @Override
    public void logout(LogoutRequest logoutRequest) {
        userRefreshTokenRepository.deleteByRefreshToken(logoutRequest.getRefreshToken());
    }

    @Transactional
    @Override
    public void deleteExpiredRefreshTokens() {
        //날짜 만료 리프레쉬 토큰
        userRefreshTokenRepository.deleteExpiredRefreshTokens();
    }
}
