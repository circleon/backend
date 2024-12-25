package com.circleon.config.dataloader;

import com.circleon.common.CommonStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.schedule.circle.entity.CircleSchedule;
import com.circleon.domain.schedule.circle.repository.CircleScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(4)
public class CircleScheduleDataLoader implements CommandLineRunner {

    private final CircleRepository circleRepository;
    private final CircleScheduleRepository circleScheduleRepository;

    @Override
    public void run(String... args) throws Exception {

        if(circleScheduleRepository.count() != 0){
            return;
        }

        List<Circle> circles = circleRepository.findAllByIdLessThanEqual(10L);

        //일정 동아리당 20개 생성
        createCircleScheduleTestData(circles);
    }

    private void createCircleScheduleTestData(List<Circle> circles){
        List<CircleSchedule> circleSchedules = new ArrayList<>();

        for(Circle circle : circles){

            for(int i = 1; i <= 20; i++){
                String title = i + "번 일정입니다.";
                String content = i + "번 일정 내용입니다.";
                LocalDateTime startAt = LocalDateTime.now().plusMonths(1).plusDays(i);
                LocalDateTime endAt = startAt.plusHours(5);

                CircleSchedule circleSchedule = CircleSchedule.builder()
                        .title(title)
                        .content(content)
                        .startAt(startAt)
                        .endAt(endAt)
                        .status(CommonStatus.ACTIVE)
                        .circle(circle)
                        .build();

                circleSchedules.add(circleSchedule);
            }

        }

        circleScheduleRepository.saveAll(circleSchedules);
    }
}
