package com.circleon.domain.circle.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.dto.MyCircleCreateRequest;
import com.circleon.domain.circle.dto.MyCircleCreateResponse;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.MyCircleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/my-circles")
public class MyCircleController {

    private final MyCircleService myCircleService;

    @PostMapping("/{circleId}")
    public ResponseEntity<MyCircleCreateResponse> applyForMembership(@LoginUser Long userId,
                                                                     @PathVariable Long circleId,
                                                                     @RequestBody MyCircleCreateRequest myCircleCreateRequest){

        MyCircleCreateResponse myCircleCreateResponse = myCircleService.applyForMembership(userId, circleId, myCircleCreateRequest);

        return ResponseEntity.ok(myCircleCreateResponse);
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
}
