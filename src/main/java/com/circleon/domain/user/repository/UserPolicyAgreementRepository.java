package com.circleon.domain.user.repository;

import com.circleon.domain.user.entity.UserPolicyAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPolicyAgreementRepository extends JpaRepository<UserPolicyAgreement, Long> {
}
