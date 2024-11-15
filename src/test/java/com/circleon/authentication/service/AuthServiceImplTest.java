package com.circleon.authentication.service;

import com.circleon.authentication.dto.LoginRequest;
import com.circleon.authentication.dto.LoginResponse;
import com.circleon.authentication.email.dto.AwsSesEmailRequest;
import com.circleon.authentication.email.dto.EmailVerificationRequest;
import com.circleon.authentication.email.dto.VerificationCodeRequest;
import com.circleon.authentication.email.service.EmailService;
import com.circleon.authentication.entity.EmailVerification;
import com.circleon.authentication.entity.RefreshToken;
import com.circleon.authentication.jwt.JwtUtil;
import com.circleon.authentication.repository.EmailVerificationRepository;
import com.circleon.authentication.repository.RefreshTokenRepository;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class AuthServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImplTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName(value = "이메일이 이미 회원가입 되어 있는 경우")
    void testSendVerificationEmailWhenEmailAlreadyExists() {
        // Given
        EmailVerificationRequest request = EmailVerificationRequest.builder()
                .email("test@example.com")
                .build();
        when(userRepository.existsByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)).thenReturn(true); // 이미 가입된 이메일

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> authServiceImpl.sendVerificationEmail(request));
        assertEquals(UserResponseStatus.EMAIL_DUPLICATE, exception.getStatus());
    }

    @Test
    @DisplayName("새로 인증 코드를 생성하고 저장")
    void testSendVerificationEmailWhenNewVerificationEmail() {
        // Given
        EmailVerificationRequest request = EmailVerificationRequest.builder()
                .email("test@example.com")
                .build();
        when(userRepository.existsByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)).thenReturn(false); // 이메일 미가입 상태
        when(emailVerificationRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty()); // 인증 데이터 없음
        String verificationCode = "123456";
        String encryptedCode = "encoded123456";
        when(emailService.generateVerificationCode()).thenReturn(verificationCode); // 인증 코드 생성
        when(passwordEncoder.encode(verificationCode)).thenReturn(encryptedCode); // 인증 코드 암호화

        // When
        authServiceImpl.sendVerificationEmail(request);

        // Then
        verify(emailVerificationRepository, times(1)).save(any(EmailVerification.class)); // 인증 정보 저장 확인
        verify(emailService, times(1)).sendEmail(any(AwsSesEmailRequest.class)); // 이메일 발송 확인
    }

    @Test
    @DisplayName("인증 코드가 유효시간이 끝나기 전에 다시 인증 코드 요청하는 경우")
    void testSendVerificationEmailWhenCodeNotExpired() {
        // Given
        EmailVerificationRequest request = EmailVerificationRequest.builder()
                .email("test@example.com")
                .build();
        when(userRepository.existsByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)).thenReturn(false);
        EmailVerification existingVerification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("654321")
                .expirationTime(LocalDateTime.now().plusMinutes(5))
                .attemptCount(1)
                .lastAttemptTime(LocalDateTime.now())
                .build();
        when(emailVerificationRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingVerification)); // 유효한 인증 정보 있음

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> authServiceImpl.sendVerificationEmail(request));
        assertEquals(UserResponseStatus.VERIFICATION_CODE_NOT_EXPIRED, exception.getStatus());
    }
    @Test
    @DisplayName("인증 코드 횟수가 하루 횟수 제한을 초과한 경우")
    void testSendVerificationEmailWhenTooManyAttempts() {
        // Given
        EmailVerificationRequest request = EmailVerificationRequest.builder()
                .email("test@example.com")
                .build();
        when(userRepository.existsByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)).thenReturn(false);
        EmailVerification existingVerification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("654321")
                .expirationTime(LocalDateTime.now().minusMinutes(5))
                .attemptCount(5)
                .lastAttemptTime(LocalDateTime.now().minusHours(1))
                .build();
        when(emailVerificationRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingVerification)); // 시도 횟수 초과 상태

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> authServiceImpl.sendVerificationEmail(request));
        assertEquals(UserResponseStatus.TOO_MANY_ATTEMPTS, exception.getStatus());
    }

    @Test
    @DisplayName("하루가 지나서 인증 코드 횟수 초기화 되는 경우")
    void testSendVerificationEmailWhenAttemptCountResetAfterOneDay() {
        // Given
        EmailVerificationRequest request = EmailVerificationRequest.builder()
                .email("test@example.com")
                .build();
        when(userRepository.existsByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)).thenReturn(false);

        // 하루 이상 지난 기존 인증 정보
        EmailVerification existingVerification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("encoded654321")
                .expirationTime(LocalDateTime.now().minusMinutes(10))
                .attemptCount(3)
                .lastAttemptTime(LocalDateTime.now().minusDays(1)) // 하루전
                .build();

        when(emailVerificationRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingVerification));

        String verificationCode = "123456";
        when(emailService.generateVerificationCode()).thenReturn(verificationCode);
        when(passwordEncoder.encode(verificationCode)).thenReturn("encoded123456");

        // When
        authServiceImpl.sendVerificationEmail(request);

        // Then
        assertEquals(1, existingVerification.getAttemptCount()); // 시도 횟수가 1로 초기화되었는지 확인
        assertEquals("encoded123456", existingVerification.getVerificationCode()); // 새로운 인증 코드가 갱신되었는지 확인
        verify(emailService, times(1)).sendEmail(any(AwsSesEmailRequest.class)); // 이메일 발송 확인
    }


    @Test
    @DisplayName("기존 인증 코드가 있을 때 정상적으로 동작하는 경우")
    void testSendVerificationEmailWhenExistingVerification() {
        // Given
        EmailVerificationRequest request = EmailVerificationRequest.builder()
                .email("test@example.com")
                .build();
        when(userRepository.existsByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)).thenReturn(false);

        EmailVerification existingVerification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("encoded654321")
                .expirationTime(LocalDateTime.now().minusMinutes(5))
                .attemptCount(1)
                .lastAttemptTime(LocalDateTime.now().minusHours(1))
                .build();
        when(emailVerificationRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingVerification));
        String verificationCode = "123456";
        when(emailService.generateVerificationCode()).thenReturn(verificationCode); // 새로운 코드 생성
        when(passwordEncoder.encode(verificationCode)).thenReturn("encoded123456");

        // When
        authServiceImpl.sendVerificationEmail(request);

        // Then
        assertEquals("encoded123456", existingVerification.getVerificationCode()); // 코드 갱신 확인
        verify(emailService, times(1)).sendEmail(any(AwsSesEmailRequest.class)); // 이메일 발송 확인
    }

    @Test
    @DisplayName("정상적으로 인증 코드 검증이 성공하는 경우")
    void testVerifyVerificationCodeWhenSuccess(){
        //Given
        VerificationCodeRequest request = VerificationCodeRequest.builder()
                .email("test@example.com")
                .code("123456")
                .build();

        EmailVerification emailVerification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("123456")
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .attemptCount(1)
                .lastAttemptTime(LocalDateTime.now())
                .build();

        when(emailVerificationRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(emailVerification));

        when(passwordEncoder.matches(request.getCode(), emailVerification.getVerificationCode())).thenReturn(true);

        //when
        authServiceImpl.verifyVerificationCode(request);

        //then
        assertEquals(emailVerification.getVerificationCode(), request.getCode());
        verify(emailVerificationRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    @DisplayName("해당 이메일로 저장된 인증코드가 없을 때 예외 발생")
    void testVerifyVerificationCodeWhenCodeNotFound() {
        //Given
        VerificationCodeRequest request = VerificationCodeRequest.builder()
                .email("test@example.com")
                .code("123456")
                .build();

        when(emailVerificationRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> authServiceImpl.verifyVerificationCode(request));

        assertEquals(UserResponseStatus.VERIFICATION_CODE_NOT_REQUESTED, exception.getStatus());
        verify(emailVerificationRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    @DisplayName("인증 코드 유효시간이 끝난 경우")
    void testVerifyVerificationCodeWhenExpiredCode() {
        // Given
        VerificationCodeRequest request = VerificationCodeRequest.builder()
                .email("test@example.com")
                .code("123456")
                .build();

        EmailVerification emailVerification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("encodedCode")
                .expirationTime(LocalDateTime.now().minusMinutes(10)) // 유효 시간 지난 상태
                .build();

        when(emailVerificationRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(emailVerification));

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> {
            authServiceImpl.verifyVerificationCode(request);
        });

        assertEquals(UserResponseStatus.VERIFICATION_CODE_EXPIRED, exception.getStatus());
        verify(emailVerificationRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    @DisplayName("인증 코드가 맞지 않는 경우")
    void testVerifyVerificationCodeWhenInvalidCode() {
        // Given
        VerificationCodeRequest request = VerificationCodeRequest.builder()
                .email("test@example.com")
                .code("wrongCode")
                .build();

        EmailVerification emailVerification = EmailVerification.builder()
                .email("test@example.com")
                .verificationCode("encodedCode")
                .expirationTime(LocalDateTime.now().plusMinutes(10)) // 유효 시간 내
                .build();

        when(emailVerificationRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(emailVerification));

        // PasswordEncoder가 코드 비교를 실패로 처리
        when(passwordEncoder.matches(request.getCode(), emailVerification.getVerificationCode()))
                .thenReturn(false);

        // When & Then
        UserException exception = assertThrows(UserException.class, () -> {
            authServiceImpl.verifyVerificationCode(request);
        });

        assertEquals(UserResponseStatus.INVALID_VERIFICATION_CODE, exception.getStatus());
        verify(emailVerificationRepository, times(1)).findByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).matches(request.getCode(), emailVerification.getVerificationCode());
    }

    @Test
    @DisplayName(value = "정상 로그인")
    void testLoginWhenSuccess(){
        
        //given
        User foundUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("test")
                .password("test")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("test")
                .build();

        when(userRepository.findByEmailAndStatus(loginRequest.getEmail(), UserStatus.ACTIVE)).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())).thenReturn(true);
        when(jwtUtil.createAccessToken(foundUser.getId(), foundUser.getRole().name())).thenReturn("access_token");
        when(jwtUtil.createRefreshToken(foundUser.getId(), foundUser.getRole().name())).thenReturn("refresh_token");
        when(jwtUtil.getExpiration("refresh_token")).thenReturn(new Date());
        String newAccessToken = jwtUtil.createAccessToken(foundUser.getId(), foundUser.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(foundUser.getId(), foundUser.getRole().name());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(foundUser)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        //when
        LoginResponse loginResponse = authServiceImpl.login(loginRequest);

        assertEquals("access_token", loginResponse.getToken().getAccessToken());
        assertEquals("refresh_token", loginResponse.getToken().getRefreshToken());
        assertEquals(foundUser.getId(), loginResponse.getUser().getUserId());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

    }

    @Test
    @DisplayName(value = "이메일 없는 경우")
    void testLoginWhenUserNotFound(){

        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("test")
                .build();
        when(userRepository.findByEmailAndStatus(loginRequest.getEmail(), UserStatus.ACTIVE)).thenReturn(Optional.empty());
        //When,then
        UserException exception = assertThrows(UserException.class, () -> authServiceImpl.login(loginRequest));
        assertEquals(UserResponseStatus.EMAIL_NOT_FOUND, exception.getStatus());

    }

    @Test
    @DisplayName(value = "비밀번호 불일치 테스트")
    void testLoginWhenPasswordMismatch(){
        //given
        User foundUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("test")
                .password("test")
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.findByEmailAndStatus(foundUser.getEmail(), UserStatus.ACTIVE)).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(foundUser.getPassword(), foundUser.getPassword())).thenReturn(false);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("test")
                .build();

        UserException exception = assertThrows(UserException.class, () -> authServiceImpl.login(loginRequest));
        assertEquals(UserResponseStatus.PASSWORD_MISMATCH, exception.getStatus());

    }


}