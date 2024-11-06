package com.circleon.domain.circle.entity;

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
public class Circle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

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
    private LocalDateTime recruitmentStartDate;

    @Column
    private LocalDateTime recruitmentEndDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private User applicant;

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
}
