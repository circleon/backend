package com.circleon.domain.admin.service;


import com.circleon.authentication.jwt.JwtUtil;
import com.circleon.domain.admin.AdminResponseStatus;
import com.circleon.domain.admin.dto.LoginRequest;
import com.circleon.domain.admin.dto.LoginResponse;
import com.circleon.domain.admin.dto.Token;
import com.circleon.domain.admin.dto.UserInfo;
import com.circleon.domain.admin.exception.AdminException;

import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.service.UserDataService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserDataService userDataService;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        User admin = userDataService.findByEmailAndStatus(loginRequest.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new AdminException(AdminResponseStatus.ADMIN_NOT_FOUND, "[login] 관리자 로그인 실패"));


        //비밀번호 검증
        if(!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())){
            throw new AdminException(AdminResponseStatus.ADMIN_NOT_FOUND, "[login] 비밀번호 불일치");
        }

        //token 생성
        String accessToken = jwtUtil.createAccessToken(admin.getId(), admin.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(admin.getId(), admin.getRole().name());

        UserInfo userInfo = UserInfo.from(admin);
        Token token = Token.of(accessToken);

        //TODO secure 설정 해야함
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .build();

        response.addHeader("Set-Cookie", responseCookie.toString());
        return LoginResponse.of(userInfo, token);
    }

    @Transactional(readOnly = true)
    public Token refresh(String refreshToken, HttpServletResponse response) {

        Long userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(userId, role);
        refreshToken = jwtUtil.createRefreshToken(userId, role);

        //TODO secure 설정 해야함
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .build();

        response.addHeader("Set-Cookie", responseCookie.toString());

        return Token.of(newAccessToken);
    }

    @Transactional
    public UserInfo getAdminInfo(Long userId) {
        User admin = userDataService.findByIdAndRole(userId, Role.ROLE_ADMIN)
                .orElseThrow(() -> new AdminException(AdminResponseStatus.ADMIN_NOT_FOUND, "[getAdminInfo] 관리자가 존재하지 않음"));

        return UserInfo.from(admin);
    }

}
