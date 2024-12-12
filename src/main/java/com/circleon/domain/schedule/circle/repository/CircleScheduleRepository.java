package com.circleon.domain.schedule.circle.repository;

import com.circleon.common.CommonStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.schedule.circle.entity.CircleSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CircleScheduleRepository extends JpaRepository<CircleSchedule, Long>, CircleScheduleRepositoryCustom {

    Optional<CircleSchedule> findByIdAndCircleAndStatus(Long id, Circle circle, CommonStatus status);
}
