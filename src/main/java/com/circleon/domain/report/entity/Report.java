package com.circleon.domain.report.entity;

import com.circleon.common.BaseEntity;
import com.circleon.domain.report.ReportType;
import com.circleon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ReportType targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(length = 1000, nullable = false)
    private String reason;

    private boolean handled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

}
