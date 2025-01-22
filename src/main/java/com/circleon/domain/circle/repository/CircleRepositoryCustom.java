package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.entity.Circle;

import java.util.List;

public interface CircleRepositoryCustom {

    void deleteCircles(List<Circle> circles);
}
