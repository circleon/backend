package com.circleon.domain.circle.controller;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.MyCircleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/my-circles")
public class MyCircleController {

    private final MyCircleService myCircleService;

    @PostMapping("/{circleId}")
    public ResponseEntity<MyCircleCreateResponse> applyForMembership(@LoginUser Long userId,
                                                                     @PathVariable Long circleId){

        MyCircleCreateResponse myCircleCreateResponse = myCircleService.applyForMembership(userId, circleId);

        return ResponseEntity.ok(myCircleCreateResponse);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<MyCircleSearchResponse>> findPagedMyCircles(@LoginUser Long userId,
                                                                                        @RequestParam MembershipStatus membershipStatus,
                                                                                        @RequestParam int page,
                                                                                        @RequestParam int size){
        //조회 가능한 멤버쉽 상태 검증
        if(!isValidMembershipStatusForSearch(membershipStatus)){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[findPagedMyCircles] 가입 상태와 대기 상태 외의 조회를 시도함.");
        }

        Sort sort = getSortByMembershipStatus(membershipStatus);

        Pageable pageable = PageRequest.of(page, size, sort);

        PageableValidator.validatePageable(pageable, List.of("createdAt", "joinedAt"), 100);

        PaginatedResponse<MyCircleSearchResponse> pagedMyCircles = myCircleService
                .findPagedMyCircles(MyCircleSearchRequest.of(userId, membershipStatus, pageable));

        return ResponseEntity.ok(pagedMyCircles);
    }

    private static Sort getSortByMembershipStatus(MembershipStatus membershipStatus) {

        return MembershipStatus.PENDING == membershipStatus
                ? Sort.by(Sort.Order.desc("createdAt"))
                : Sort.by(Sort.Order.desc("joinedAt"));
    }

    private boolean isValidMembershipStatusForSearch(MembershipStatus membershipStatus) {
        return MembershipStatus.APPROVED == membershipStatus || MembershipStatus.PENDING == membershipStatus;
    }

    @DeleteMapping("/{memberId}/application")
    public ResponseEntity<SuccessResponse> deleteApplication(@LoginUser Long userId,
                                                             @PathVariable Long memberId){
        myCircleService.deleteApplication(userId, memberId);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @PostMapping("/{memberId}/leave-request")
    public ResponseEntity<SuccessResponse> requestLeave(@LoginUser Long userId,
                                                        @PathVariable Long memberId,
                                                        @RequestBody @Valid CircleLeaveRequest circleLeaveRequest){

        myCircleService.processLeaveRequest(userId, memberId, circleLeaveRequest);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

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
