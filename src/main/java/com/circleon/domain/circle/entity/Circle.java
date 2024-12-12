package com.circleon.domain.circle.entity;

import com.circleon.common.BaseEntity;
import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleStatus;
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
public class Circle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String profileImgUrl;

    @Column
    private String thumbnailUrl;

    @Column
    private String introImgUrl;

    @Column(nullable = false)
    private CircleStatus circleStatus;

    @Column
    private CategoryType categoryType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column
    private String summary;

    @Column
    private LocalDateTime recruitmentStartDate;

    @Column
    private LocalDateTime recruitmentEndDate;

    @Column(nullable = false)
    private int memberCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    public void incrementMemberCount() {
        this.memberCount++;
    }

    public void decrementMemberCount() {
        if (this.memberCount > 0) {
            this.memberCount--;
        }

    }
}
