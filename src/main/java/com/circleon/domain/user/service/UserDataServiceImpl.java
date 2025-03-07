package com.circleon.domain.user.service;

import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.entity.Role;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmailAndStatus(String email, UserStatus status) {
        return userRepository.findByEmailAndStatus(email, status);
    }

    @Override
    public Optional<User> findByIdAndStatus(Long id, UserStatus status) {
        return userRepository.findByIdAndStatus(id, status);
    }

    @Override
    public boolean existsByIdAndStatus(Long id, UserStatus status) {
        return userRepository.existsByIdAndStatus(id, status);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByIdAndRole(Long id, Role role) {
        return userRepository.findByIdAndRole(id, role);
    }
}
