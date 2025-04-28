package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CircleRepository extends JpaRepository<Circle, Long>, CircleRepositoryCustom {

    Optional<Circle> findByIdAndCircleStatus(Long id, CircleStatus circleStatus);

    List<Circle> findAllByCircleStatus(CircleStatus circleStatus);

    Page<Circle> findAllByCircleStatus(CircleStatus circleStatus, Pageable pageable);

    Page<Circle> findAllByCategoryTypeAndCircleStatus(CategoryType categoryType, CircleStatus circleStatus, Pageable pageable);

    Page<Circle> findAllByCircleStatusAndCategoryTypeAndOfficialStatus(CircleStatus circleStatus, CategoryType categoryType, OfficialStatus officialStatus, Pageable pageable);

    Page<Circle> findAllByCircleStatusAndOfficialStatus(CircleStatus circleStatus, OfficialStatus officialStatus, Pageable pageable);

    //데이터로더 용
    List<Circle> findAllByIdLessThanEqual(Long id);
}
