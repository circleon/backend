package com.circleon.domain.circle.entity;

import com.circleon.domain.circle.CircleStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CircleStatus status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String introduction;


    @Builder
    public Circle(String name, String profileImgUrl, String thumbnailUrl, CircleStatus status, String introduction) {
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;
        this.introduction = introduction;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
}
