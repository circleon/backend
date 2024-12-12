package com.circleon.domain.circle.dto;


import com.circleon.domain.circle.MembershipStatus;
import lombok.*;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCircleSearchRequest {

    private Long userId;

    private MembershipStatus membershipStatus;

    private Pageable pageable;

    public static MyCircleSearchRequest of(Long userId, MembershipStatus membershipStatus, Pageable pageable) {
        return MyCircleSearchRequest.builder()
                .userId(userId)
                .membershipStatus(membershipStatus)
                .pageable(pageable)
                .build();
    }
}
