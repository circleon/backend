package com.circleon.domain.schedule.circle.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.schedule.ScheduleResponseStatus;
import com.circleon.domain.schedule.circle.dto.*;
import com.circleon.domain.schedule.circle.service.CircleScheduleService;
import com.circleon.domain.schedule.exception.ScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CircleScheduleController {

    private final CircleScheduleService circleScheduleService;

    @PostMapping("/circles/{circleId}/schedules")
    public ResponseEntity<CircleScheduleCreateResponse> createSchedule(@LoginUser Long userId,
                                                                       @PathVariable Long circleId,
                                                                       @RequestBody CircleScheduleCreateRequest circleScheduleCreateRequest){

        CircleScheduleCreateResponse circleScheduleCreateResponse = circleScheduleService
                .createCircleSchedule(CircleMemberIdentifier.of(userId, circleId), circleScheduleCreateRequest);

        return ResponseEntity.ok(circleScheduleCreateResponse);
    }

    @GetMapping("/circles/{circleId}/schedules")
    public ResponseEntity<CircleMonthlySchedules> findMonthlySchedules(@LoginUser Long userId,
                                                                       @PathVariable Long circleId,
                                                                       @RequestParam int year,
                                                                       @RequestParam int month
                                                                       ){
        CircleMonthlySchedules circleMonthlySchedules = circleScheduleService
                .findMonthlySchedules(CircleMemberIdentifier.of(userId, circleId), year, month);

        return ResponseEntity.ok(circleMonthlySchedules);
    }

    @PutMapping("/circles/{circleId}/schedules/{circleScheduleId}")
    public ResponseEntity<CircleScheduleUpdateResponse> updateCircleSchedule(@LoginUser Long userId,
                                                                             @PathVariable Long circleId,
                                                                             @PathVariable Long circleScheduleId,
                                                                             @RequestBody CircleScheduleUpdateRequest circleScheduleUpdateRequest){
        CircleScheduleUpdateResponse circleScheduleUpdateResponse = circleScheduleService
                .updateCircleSchedule(CircleMemberIdentifier.of(userId, circleId), circleScheduleId, circleScheduleUpdateRequest);

        return ResponseEntity.ok(circleScheduleUpdateResponse);
    }

    @DeleteMapping("/circles/{circleId}/schedules/{circleScheduleId}")
    public ResponseEntity<SuccessResponse> deleteCircleSchedule(@LoginUser Long userId,
                                                                @PathVariable Long circleId,
                                                                @PathVariable Long circleScheduleId){

        circleScheduleService.deleteCircleSchedule(CircleMemberIdentifier.of(userId, circleId), circleScheduleId);

        return ResponseEntity.ok(SuccessResponse.builder().message("success").build());
    }

    @GetMapping("/circles/{circleId}/schedules/next")
    public ResponseEntity<CircleScheduleDetail> findNextSchedule(@LoginUser Long userId,
                                                                 @PathVariable Long circleId){

        CircleScheduleDetail nextSchedule = circleScheduleService.findNextSchedule(CircleMemberIdentifier.of(userId, circleId));

        return ResponseEntity.ok(nextSchedule);
    }


    @ExceptionHandler(CircleException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(CircleException e) {

        CircleResponseStatus status = e.getStatus();

        log.warn("CircleException: {}", e.getMessage());

        log.warn("CircleException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());


        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(ScheduleException.class)
    public ResponseEntity<ErrorResponse> handleScheduleException(ScheduleException e) {

        ScheduleResponseStatus status = e.getStatus();

        log.warn("ScheduleException: {}", e.getMessage());

        log.warn("ScheduleException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }
}
