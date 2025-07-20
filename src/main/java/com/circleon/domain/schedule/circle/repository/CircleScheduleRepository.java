package com.circleon.domain.schedule.circle.repository;

import com.circleon.common.CommonStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.schedule.circle.entity.CircleSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CircleScheduleRepository extends JpaRepository<CircleSchedule, Long>, CircleScheduleRepositoryCustom {

    Optional<CircleSchedule> findByIdAndCircleAndStatus(Long id, Circle circle, CommonStatus status);

    Optional<CircleSchedule> findFirstByCircleAndStartAtBetweenOrderByStartAt(Circle circle, LocalDateTime startAt, LocalDateTime endAt);

    List<CircleSchedule> findAllByCircleIn(List<Circle> circles);
}
