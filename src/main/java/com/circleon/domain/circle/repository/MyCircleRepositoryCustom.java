package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MyCircleRepositoryCustom {

    Page<MyCircle> findAllByCircleAndMembershipStatusWithUser(Circle circle, MembershipStatus membershipStatus, Pageable pageable);

}
