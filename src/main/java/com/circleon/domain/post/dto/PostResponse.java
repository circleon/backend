package com.circleon.domain.post.dto;

import com.circleon.domain.post.PostType;
import com.circleon.domain.post.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {

    private Long postId;

    private Boolean isPinned;

    private String postImgUrl;

    private String content;

    private PostType postType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int commentCount;

    private Author author;

}
