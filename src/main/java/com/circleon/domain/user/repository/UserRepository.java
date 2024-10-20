package com.circleon.domain.user.repository;

import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailAndStatus(String email, UserStatus status);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}
