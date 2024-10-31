package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyCircleRepository extends JpaRepository<MyCircle, Long> {

    Optional<MyCircle> findByUserAndCircleAndMembershipStatus(User user, Circle circle, MembershipStatus membershipStatus);

    int countByCircleAndMembershipStatus(Circle circle, MembershipStatus membershipStatus);
}
