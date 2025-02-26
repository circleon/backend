package com.circleon.domain.circle.controller;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.ErrorResponse;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;
import com.circleon.domain.circle.CategoryType;

import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.CircleMemberService;
import com.circleon.domain.circle.service.CircleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/circles")
public class CircleController {

    private final CircleService circleService;
    private final FileStore circleFileStore;
    private final CircleMemberService circleMemberService;

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

    @GetMapping("/images/{circleId}/{directory}/{filename}")
    public ResponseEntity<Resource> findImage(@PathVariable Long circleId,
                                              @PathVariable String directory,
                                              @PathVariable String filename){

        String filePath = circleId + "/" + directory + "/" + filename;
        Resource resource = circleService.loadImageAsResource(filePath);
        String extension = circleFileStore.extractExtension(filename);

        MediaType mediaType;
        if(extension.equals("png")){
            mediaType = MediaType.IMAGE_PNG;
        }else{
            mediaType = MediaType.IMAGE_JPEG;
        }

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

        PageableValidator.validatePageable(pageable, List.of("joinedAt", "username"), 1000);

        Page<CircleMemberResponse> pagedCircleMembers = circleService.findPagedCircleMembers(userId, circleId, pageable, membershipStatus);

        return ResponseEntity.ok(PaginatedResponse.fromPage(pagedCircleMembers));
    }

    private boolean isAccessMembershipStatus(MembershipStatus membershipStatus) {
        return membershipStatus == MembershipStatus.APPROVED || membershipStatus == MembershipStatus.PENDING || membershipStatus == MembershipStatus.LEAVE_REQUEST;
}

    @PutMapping("/{circleId}/members/{memberId}/role")
    public ResponseEntity<SuccessResponse> updateCircleMemberRole(@LoginUser Long userId,
                                                            @PathVariable Long circleId,
                                                            @PathVariable Long memberId,
                                                            @Valid @RequestBody CircleRoleUpdateRequest circleRoleUpdateRequest){
        circleMemberService.updateCircleMemberRole(userId, circleId, memberId, circleRoleUpdateRequest);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @PutMapping("/{circleId}/members/{memberId}/status")
    public ResponseEntity<SuccessResponse> updateMembershipStatus(@LoginUser Long userId,
                                                                  @PathVariable Long circleId,
                                                                  @PathVariable Long memberId,
                                                                  @Valid @RequestBody MembershipStatusUpdateRequest membershipStatusUpdateRequest){



        circleMemberService.updateMembershipStatus(userId, circleId, memberId, membershipStatusUpdateRequest);

        return ResponseEntity.ok(SuccessResponse.builder().message("Success").build());
    }

    @DeleteMapping("/{circleId}/members/{memberId}")
    public ResponseEntity<SuccessResponse> expelMember(@LoginUser Long userId,
                                                       @PathVariable Long circleId,
                                                       @PathVariable Long memberId){

        circleMemberService.expelMember(userId, circleId, memberId);

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

