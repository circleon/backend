package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.entity.Circle;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.circleon.domain.circle.entity.QCircle.circle;

@Repository
@RequiredArgsConstructor
public class CircleRepositoryImpl implements CircleRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteCircles(List<Circle> circles) {
        jpaQueryFactory
                .delete(circle)
                .where(circle.in(circles))
                .execute();
    }
}
