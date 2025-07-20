package com.circleon.domain.schedule.circle.repository;

import com.circleon.common.CommonStatus;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.schedule.circle.dto.CircleInfo;
import com.circleon.domain.schedule.circle.dto.CircleScheduleDetail;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.circleon.domain.circle.entity.QCircle.circle;
import static com.circleon.domain.schedule.circle.entity.QCircleSchedule.circleSchedule;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CircleScheduleRepositoryImpl implements CircleScheduleRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CircleScheduleDetail> findSchedulesWithinDateRange(Long circleId, LocalDateTime startAt, LocalDateTime endAt) {

        return jpaQueryFactory.select(Projections.constructor(CircleScheduleDetail.class,
                        circleSchedule.id,
                        circleSchedule.title,
                        circleSchedule.content,
                        circleSchedule.startAt,
                        circleSchedule.endAt
                ))
                .from(circleSchedule)
                .where(
                        circleIdEq(circleId),
                        circleStatusIsActive(),
                        dateRangeCondition(startAt, endAt),
                        circleScheduleIsActive()
                )
                .orderBy(circleSchedule.startAt.asc())
                .fetch();
    }

    private BooleanExpression circleScheduleIsActive() {
        return circleSchedule.status.eq(CommonStatus.ACTIVE);
    }

    private BooleanExpression circleIdEq(Long circleId) {
        return circleId != null ? circleSchedule.circle.id.eq(circleId) : null;
    }

    private BooleanExpression circleStatusIsActive(){
        return circleSchedule.circle.circleStatus.eq(CircleStatus.ACTIVE);
    }

    private BooleanExpression dateRangeCondition(LocalDateTime startAt, LocalDateTime endAt) {
        return circleSchedule.startAt.between(startAt, endAt).or(circleSchedule.endAt.between(startAt, endAt));
    }

    @Override
    public CircleInfo findCircleInfo(Long circleId) {
        return jpaQueryFactory.select(Projections.constructor(CircleInfo.class,
                        circle.id,
                        circle.name
                ))
                .from(circle)
                .where(
                        circle.id.eq(circleId),
                        circle.circleStatus.eq(CircleStatus.ACTIVE)
                ).fetchOne();
    }

    @Override
    public void deleteAllByCircles(List<Circle> circles) {
        jpaQueryFactory
                .delete(circleSchedule)
                .where(circleSchedule.circle.in(circles))
                .execute();
    }
}
