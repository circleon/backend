package com.circleon.domain.admin.controller;

import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.admin.dto.CircleResponse;
import com.circleon.domain.admin.dto.CircleUpdateOfficialRequest;
import com.circleon.domain.admin.service.AdminCircleService;
import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.exception.CircleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
public class AdminCircleController {

    private final AdminCircleService adminCircleService;

    @GetMapping("/circles")
    public ResponseEntity<PaginatedResponse<CircleResponse>> findCirclesByOfficialStatus(
            @RequestParam OfficialStatus officialStatus,
            @RequestParam CategoryType categoryType,
            Pageable pageable
            ){
        PaginatedResponse<CircleResponse> circleResponse = adminCircleService.findPagedCirclesByOfficialStatus(officialStatus, categoryType, pageable);

        return ResponseEntity.ok(circleResponse);
    }

    //인증 상태 변경
    @PutMapping("/circles/{circleId}/officialStatus")
    public ResponseEntity<SuccessResponse> updateOfficialStatus(@PathVariable Long circleId,
                                                                @RequestBody CircleUpdateOfficialRequest updateOfficialRequest){

        adminCircleService.updateOfficialStatus(circleId, updateOfficialRequest);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    //삭제
    @DeleteMapping("/circles/{circleId}")
    public ResponseEntity<SuccessResponse> deleteCircle(@PathVariable Long circleId){

        adminCircleService.deleteCircle(circleId);

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
