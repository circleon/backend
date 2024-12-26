package com.circleon.domain.circle.entity;

import com.circleon.common.BaseEntity;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCircle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime joinedAt;

    @Column
    private CircleRole circleRole;

    @Column(nullable = false)
    private MembershipStatus membershipStatus;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String joinMessage;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String leaveMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id", nullable = false)
    private Circle circle;

    public void initJoinedAt() {
        this.joinedAt = LocalDateTime.now();
    }

}
