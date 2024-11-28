package com.circleon.domain.post.entity;

import com.circleon.common.BaseEntity;
import com.circleon.common.CommonStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.post.PostType;
import com.circleon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private PostType postType;

    @Column
    private CommonStatus status;

    @Column
    @Builder.Default
    private Integer commentCount = 0;

    @Column
    @Builder.Default
    private Boolean isPinned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id")
    private Circle circle;

}
