package com.circleon.authentication.service;

import com.circleon.authentication.AuthConstants;
import com.circleon.authentication.dto.*;
import com.circleon.authentication.entity.UserRefreshToken;
import com.circleon.authentication.jwt.JwtUtil;
import com.circleon.authentication.repository.UserRefreshTokenRepository;
import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.user.UserResponseStatus;
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
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService{

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
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

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        user.withdraw();
    }

    //TODO 좀 더 고민
    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        userRefreshTokenRepository.deleteByRefreshToken(logoutRequest.getRefreshToken());
    }

    @Transactional
    public void deleteExpiredRefreshTokens() {
        //날짜 만료 리프레쉬 토큰
        userRefreshTokenRepository.deleteExpiredRefreshTokens();
    }
}
