package com.circleon.domain.circle.controller;

import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.dto.CircleCreateRequest;
import com.circleon.domain.circle.dto.CircleResponse;
import com.circleon.domain.circle.dto.CircleUpdateRequest;
import com.circleon.domain.circle.dto.CircleUpdateResponse;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.CircleService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<PaginatedResponse<CircleResponse>> findCircles(@LoginUser Long userId,
                                                                         @RequestParam(required = false) CategoryType categoryType,
                                                                         Pageable pageable) {

        Page<CircleResponse> circlesPage = circleService.findCircles(userId, pageable, categoryType);

        List<CircleResponse> content = circlesPage.getContent();
        int currentPageNumber = circlesPage.getNumber();
        long totalElementCount = circlesPage.getTotalElements();
        int totalPageCount = circlesPage.getTotalPages();

        return ResponseEntity.ok(PaginatedResponse.of(content, currentPageNumber, totalElementCount, totalPageCount));
    }

    @PutMapping("/{circleId}")
    public ResponseEntity<CircleUpdateResponse> updateCircle(@Valid @ModelAttribute CircleUpdateRequest circleUpdateRequest,
                                                        @LoginUser Long userId,
                                                        @PathVariable Long circleId) {
        CircleUpdateResponse circleUpdateResponse = circleService.updateCircle(userId, circleId, circleUpdateRequest);

        return ResponseEntity.ok(circleUpdateResponse);
    }

    @ExceptionHandler(CircleException.class)
    public ResponseEntity<ErrorResponse> handleCircleException(CircleException e) {

        CircleResponseStatus status = e.getStatus();

        log.warn("CircleResponseStatus: {} {} {}", status.getHttpStatusCode(), status.getCode(), status.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.getCode())
                .errorMessage(status.getMessage())
                .build();

        return ResponseEntity.status(status.getHttpStatusCode()).body(errorResponse);
    }
}
