package com.circleon.domain.schedule.circle.service;

import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.schedule.circle.repository.CircleScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CircleScheduleDataServiceImpl implements CircleScheduleDataService {

    private final CircleScheduleRepository circleScheduleRepository;

    @Override
    public void deleteAllByCircles(List<Circle> circles) {
        circleScheduleRepository.deleteAllByCircles(circles);
    }
}
