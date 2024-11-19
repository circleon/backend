package com.circleon.domain.user.service;

import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;

import java.util.Optional;

public interface UserService {

    User findByEmailAndStatus(String email, UserStatus status);

    Optional<User> findByIdAndStatus(Long id, UserStatus status);

    Optional<User> findById(Long id);

    boolean existsByIdAndStatus(Long id, UserStatus status);
}
