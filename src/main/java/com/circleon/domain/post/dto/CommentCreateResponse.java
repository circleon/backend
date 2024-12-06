package com.circleon.domain.post.dto;

import com.circleon.domain.post.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateResponse {

    private Long commentId;

    private String content;

    private LocalDateTime createdAt;

    private Author author;

    public static CommentCreateResponse fromComment(Comment comment) {
        return CommentCreateResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .author(Author.builder()
                        .authorId(comment.getAuthor().getId())
                        .authorName(comment.getAuthor().getUsername())
                        .authorProfileUrl(comment.getAuthor().getProfileImgUrl())
                        .build()
                ).build();
    }
}
