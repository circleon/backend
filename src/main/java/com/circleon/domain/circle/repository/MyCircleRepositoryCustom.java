package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MyCircleRepositoryCustom {

    Page<MyCircle> findAllByCircleAndMembershipStatusWithUser(Circle circle, MembershipStatus membershipStatus, Pageable pageable);

    Optional<MyCircle> findAllByUserAndCircleInMembershipStatuses(User user, Circle circle, List<MembershipStatus> membershipStatuses);
}
