package com.circleon.domain.circle;

import com.circleon.domain.circle.service.CircleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CircleScheduler {

    private final CircleService circleService;

    @Scheduled(cron = "0 0 3 ? * TUE")
    public void cleanUpSoftDeletedCircles(){
        try{
            log.info("Cleaning up SoftDeleted Circles");
            circleService.deleteSoftDeletedCircles();
        } catch (Exception e){
            log.error("Cleaning up SoftDeleted Circles fail", e);
        }
    }
}
