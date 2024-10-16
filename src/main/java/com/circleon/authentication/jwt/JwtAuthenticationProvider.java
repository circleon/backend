package com.circleon.authentication.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider  implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = (String) authentication.getCredentials();

        if(token != null && jwtUtil.validateToken(token)) {

            try{
                Long userId = jwtUtil.getUserId(token);
                String role = jwtUtil.getRole(token);

                return new JwtAuthenticationToken(userId, List.of(()->role));
            }catch (ExpiredJwtException e) {
                throw new AuthenticationException("JWT 토큰 검증 실패") {};
            }

        }else{
            throw new AuthenticationException("JWT 토큰 검증 실패") {};
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
