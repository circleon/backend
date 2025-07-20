package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyCircleRepository extends JpaRepository<MyCircle, Long>, MyCircleRepositoryCustom {

    Optional<MyCircle> findByUserAndCircleAndMembershipStatus(User user, Circle circle, MembershipStatus membershipStatus);

    int countByCircleAndMembershipStatus(Circle circle, MembershipStatus membershipStatus);

    List<MyCircle> findAllByCircleAndMembershipStatus(Circle circle, MembershipStatus membershipStatus);

    Optional<MyCircle> findByIdAndCircleAndMembershipStatus(Long id, Circle circle, MembershipStatus membershipStatus);

    Optional<MyCircle> findByIdAndCircle(Long id, Circle circle);

    List<MyCircle> findAllByUserAndMembershipStatus(User user, MembershipStatus membershipStatus);

    Optional<MyCircle> findFirstByUserAndCircleRoleAndMembershipStatus(
            User user,
            CircleRole circleRole,
            MembershipStatus membershipStatus
    );

    //데이터로더 용
    List<MyCircle> findAllByIdLessThanEqual(Long id);
}
