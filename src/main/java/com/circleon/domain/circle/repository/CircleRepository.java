package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.entity.Circle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CircleRepository extends JpaRepository<Circle, Long> {
}
