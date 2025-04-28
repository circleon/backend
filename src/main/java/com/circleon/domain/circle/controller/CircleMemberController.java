package com.circleon.domain.circle.controller;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.PageableValidator;
import com.circleon.common.annotation.LoginUser;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.dto.SuccessResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.*;
import com.circleon.domain.circle.service.CircleMemberService;
import com.circleon.domain.circle.service.CircleService;
import jakarta.validation.Valid;
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
public class CircleMemberController {

    private final CircleMemberService circleMemberService;
    private final CircleService circleService;

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

    @GetMapping("/{circleId}/members/{memberId}/leave-message")
    public ResponseEntity<CircleLeaveMessage> findLeaveMessage(@LoginUser Long userId,
                                                               @PathVariable Long circleId,
                                                               @PathVariable Long memberId){
        CircleLeaveMessage leaveMessage = circleMemberService.findLeaveMessage(userId, circleId, memberId);

        return ResponseEntity.ok(leaveMessage);
    }

    @GetMapping("/{circleId}/members/{memberId}/join-message")
    public ResponseEntity<CircleJoinMessage> findJoinMessage(@LoginUser Long userId,
                                                             @PathVariable Long circleId,
                                                             @PathVariable Long memberId){

        CircleJoinMessage joinMessage = circleMemberService.findJoinMessage(userId, circleId, memberId);

        return ResponseEntity.ok(joinMessage);
    }
}
