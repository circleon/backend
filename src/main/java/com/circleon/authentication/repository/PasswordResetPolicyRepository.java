package com.circleon.authentication.repository;

import com.circleon.authentication.entity.PasswordResetPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetPolicyRepository extends JpaRepository<PasswordResetPolicy, Long> {

    Optional<PasswordResetPolicy> findByUserId(Long userId);

    Optional<PasswordResetPolicy> findByPublicId(String publicId);
}
