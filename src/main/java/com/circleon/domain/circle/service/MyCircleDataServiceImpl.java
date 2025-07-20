package com.circleon.domain.circle.service;

import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.repository.MyCircleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MyCircleDataServiceImpl implements MyCircleDataService {

    private final MyCircleRepository myCircleRepository;

    @Override
    public Optional<MyCircle> findJoinedMember(Long userId, Long circleId) {
        return myCircleRepository.findJoinedMember(userId, circleId);
    }
}
