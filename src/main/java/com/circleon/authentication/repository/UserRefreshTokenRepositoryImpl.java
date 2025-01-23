package com.circleon.authentication.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.circleon.authentication.entity.QUserRefreshToken.userRefreshToken;


@Repository
@RequiredArgsConstructor
public class UserRefreshTokenRepositoryImpl implements UserRefreshTokenRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteExpiredRefreshTokens() {
        jpaQueryFactory
                .delete(userRefreshToken)
                .where(userRefreshToken.expiresAt.before(LocalDateTime.now()))
                .execute();
    }
}
