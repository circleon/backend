package com.circleon.domain.post.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentSearchResponse {

    private Long commentId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Author author;
}
