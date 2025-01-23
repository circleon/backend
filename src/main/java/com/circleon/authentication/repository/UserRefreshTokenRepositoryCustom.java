package com.circleon.authentication.repository;

public interface UserRefreshTokenRepositoryCustom {

    void deleteExpiredRefreshTokens();
}
