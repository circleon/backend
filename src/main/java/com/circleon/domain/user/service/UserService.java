package com.circleon.domain.user.service;

import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;

public interface UserService {

    User findByEmailAndStatus(String email, UserStatus status);

    User findByIdAndStatus(Long id, UserStatus status);

    boolean existsByIdAndStatus(Long id, UserStatus status);
}
