package com.circleon.authentication.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private Key key;

    //TODO 잠시 1시간으로
    private final long jwtAccessExpirationMs = 1000L * 60 * 60;

    //TODO 잠시 1주으로
    private final long jwtRefreshExpirationMs = 1000L * 60 * 60 * 24 * 7;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        log.info("jwt secretKey init");
    }


    public String createAccessToken(Long id, String role) {
        Claims claims = Jwts.claims();
        claims.setSubject(id.toString());
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtAccessExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long id, String role) {
        Claims claims = Jwts.claims();
        claims.setSubject(id.toString());
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtRefreshExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().get("role", String.class);
    }

    public Long getUserId(String token) {
        return Long.valueOf(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject());
    }

    public Date getExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public String resolveToken(String bearerToken) {

        return bearerToken.substring(7);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException e){
            log.warn("token expired");
        }
        catch (Exception e) {
            log.warn("token invalid");
        }
        return false;
    }

}
