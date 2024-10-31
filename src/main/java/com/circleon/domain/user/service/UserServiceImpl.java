package com.circleon.domain.user.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmailAndStatus(String email, UserStatus status) {
        return userRepository.findByEmailAndStatus(email, status)
                .orElseThrow(()->new UserException(UserResponseStatus.EMAIL_NOT_FOUND));
    }

    @Override
    public User findByIdAndStatus(Long id, UserStatus status) {
        return userRepository.findByIdAndStatus(id, status)
                .orElseThrow(()->new CommonException(CommonResponseStatus.USER_NOT_FOUND));
    }

    @Override
    public boolean existsByIdAndStatus(Long id, UserStatus status) {
        return userRepository.existsByIdAndStatus(id, status);
    }
}
