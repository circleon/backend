package com.circleon.domain.circle.service;

import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.repository.CircleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CircleDataServiceImpl implements CircleDataService {

    private final CircleRepository circleRepository;

    @Override
    public Optional<Circle> findByIdAndCircleStatus(Long circleId, CircleStatus circleStatus) {
        return circleRepository.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE);
    }
}
