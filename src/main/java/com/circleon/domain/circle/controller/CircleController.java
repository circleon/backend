package com.circleon.domain.circle.controller;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.CircleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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

        Page<CircleResponse> circlesPage = circleService.findPagedCircles(pageable, categoryType);

        List<CircleResponse> content = circlesPage.getContent();
        int currentPageNumber = circlesPage.getNumber();
        long totalElementCount = circlesPage.getTotalElements();
        int totalPageCount = circlesPage.getTotalPages();

        return ResponseEntity.ok(PaginatedResponse.of(content, currentPageNumber, totalElementCount, totalPageCount));
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

    @PutMapping("/{circleId}/images")
    public ResponseEntity<CircleImagesUpdateResponse> updateCircleImages(@Valid @ModelAttribute CircleImagesUpdateRequest circleImagesUpdateRequest,
                                                                         @LoginUser Long userId,
                                                                         @PathVariable Long circleId){

        CircleImagesUpdateResponse circleImagesUpdateResponse = circleService.updateCircleImages(userId, circleId, circleImagesUpdateRequest);

        return ResponseEntity.ok(circleImagesUpdateResponse);
    }

    @DeleteMapping("/{circleId}/images")
    public ResponseEntity<SuccessResponse> deleteCircleImages(@LoginUser Long userId,
                                                              @PathVariable Long circleId,
                                                              @RequestParam boolean deleteProfileImg,
                                                              @RequestParam boolean deleteIntroImg){

        circleService.deleteCircleImages(userId, circleId, deleteProfileImg, deleteIntroImg);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @GetMapping("/images/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable String directory,
                                              @PathVariable String filename,
                                              @RequestHeader("Content-Type") String contentTypeHeader) {
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(contentTypeHeader);
        }catch (InvalidMediaTypeException e){
            throw new CommonException(CommonResponseStatus.BAD_REQUEST, e.getMessage());
        }
        String filePath = directory + "/" + filename + "." + mediaType.getSubtype();

        Resource resource = circleService.loadImageAsResource(filePath);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

    @GetMapping("/{circleId}/members")
    public ResponseEntity<PaginatedResponse<CircleMemberResponse>> findPagedCircleMembers(@LoginUser Long userId,
                                                                                   @PathVariable Long circleId,
                                                                                   @RequestParam(defaultValue = "APPROVED") MembershipStatus membershipStatus,
                                                                                   Pageable pageable) {
        if(!isAccessMembershipStatus(membershipStatus)){
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, "[findPagedCircleMembers] 동아리원 명단 조회에서 권한이 없는 접근");
        }

        Page<CircleMemberResponse> pagedCircleMembers = circleService.findPagedCircleMembers(userId, circleId, pageable, membershipStatus);

        return ResponseEntity.ok(PaginatedResponse.fromPage(pagedCircleMembers));
    }

    private boolean isAccessMembershipStatus(MembershipStatus membershipStatus) {
        return membershipStatus == MembershipStatus.APPROVED || membershipStatus == MembershipStatus.PENDING;
    }

    @PutMapping("/{circleId}/members/{memberId}/role")
    public ResponseEntity<SuccessResponse> updateCircleMemberRole(@LoginUser Long userId,
                                                            @PathVariable Long circleId,
                                                            @PathVariable Long memberId,
                                                            @RequestBody CircleRoleUpdateRequest circleRoleUpdateRequest){
        circleService.updateCircleMemberRole(userId, circleId, memberId, circleRoleUpdateRequest);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @PutMapping("/{circleId}/members/{memberId}/status")
    public ResponseEntity<SuccessResponse> updateMembershipStatus(@LoginUser Long userId,
                                                                  @PathVariable Long circleId,
                                                                  @PathVariable Long memberId,
                                                                  @RequestBody MembershipStatusUpdateRequest membershipStatusUpdateRequest){

        if(Objects.isNull(membershipStatusUpdateRequest.getApproved())){
            membershipStatusUpdateRequest.setApproved(false);
        }

        circleService.updateMembershipStatus(userId, circleId, memberId, membershipStatusUpdateRequest.getApproved());

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @DeleteMapping("/{circleId}/members/{memberId}")
    public ResponseEntity<SuccessResponse> expelMember(@LoginUser Long userId,
                                                       @PathVariable Long circleId,
                                                       @PathVariable Long memberId){

        circleService.expelMember(userId, circleId, memberId);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
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
