package com.circleon.domain.circle.controller;


import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;


import com.circleon.domain.circle.CategoryType;

import com.circleon.domain.circle.CircleResponseStatus;

import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.exception.CircleException;

import com.circleon.domain.circle.service.CircleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/circles")
public class CircleController {

    private final CircleService circleService;

    @PostMapping
    public ResponseEntity<SuccessResponse> createCircle(@Valid @ModelAttribute CircleCreateRequest circleCreateRequest,
                                                        @LoginUser Long userId) {

        circleService.createCircle(userId, circleCreateRequest);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<CircleResponse>> findPagedCircles(@RequestParam(required = false) CategoryType categoryType,
                                                                              Pageable pageable) {

        PageableValidator.validatePageable(pageable, List.of("createdAt"), 100);

        return ResponseEntity.ok(circleService.findPagedCircles(pageable, categoryType));
    }

    @GetMapping("/{circleId}")
    public ResponseEntity<CircleDetailResponse> findCircleDetail(@LoginUser Long userId,
                                                                 @PathVariable Long circleId) {
        CircleDetailResponse circleDetail = circleService.findCircleDetail(userId, circleId);

        return ResponseEntity.ok(circleDetail);
    }

    @GetMapping("/summary")
    public ResponseEntity<CircleListResponse<CircleSimpleResponse>> findAllCirclesSimple() {

        List<CircleSimpleResponse> circlesSimple = circleService.findAllCirclesSimple();

        return ResponseEntity.ok(CircleListResponse.fromList(circlesSimple));
    }

    @PutMapping("/{circleId}")
    public ResponseEntity<CircleInfoUpdateResponse> updateCircleInfo(@Valid @RequestBody CircleInfoUpdateRequest circleInfoUpdateRequest,
                                                                     @LoginUser Long userId,
                                                                     @PathVariable Long circleId) {
        CircleInfoUpdateResponse circleInfoUpdateResponse = circleService.updateCircleInfo(userId, circleId, circleInfoUpdateRequest);

        return ResponseEntity.ok(circleInfoUpdateResponse);
    }



    @PutMapping("/{circleId}/official")
    public ResponseEntity<SuccessResponse> updateOfficial(@LoginUser Long userId,
                                                          @PathVariable Long circleId,
                                                          @RequestParam(defaultValue = "UNOFFICIAL") OfficialStatus officialStatus){

        circleService.updateOfficialStatus(userId, circleId, officialStatus);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

//    @PutMapping("/{circleId}/recruiting")
//    public ResponseEntity<SuccessResponse> updateRecruitingStatus(@LoginUser Long userId,
//                                                                  @PathVariable Long circleId,
//                                                                  @RequestBody RecruitingStatusUpdateRequest recruitingStatusUpdateRequest){
//
//        circleService.updateRecruitingStatus(userId, circleId, recruitingStatusUpdateRequest);
//
//        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
//    }

    @ExceptionHandler(CircleException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(CircleException e) {

        CircleResponseStatus status = e.getStatus();

        log.error("CircleException: {}", e.getMessage());

        log.error("CircleException: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());


        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }
}

