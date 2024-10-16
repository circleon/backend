package com.circleon.authentication.jwt;

import lombok.Builder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken  extends AbstractAuthenticationToken {

    private final String token;

    private final Long userId;

    @Builder
    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.userId = 0L;
        setAuthenticated(false);
    }

    @Builder
    public JwtAuthenticationToken(Long userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.token = null;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
