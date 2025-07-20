package com.circleon.domain.post.dto;

import com.circleon.domain.post.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateResponse {

    private Long commentId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Author author;

    public static CommentUpdateResponse fromComment(Comment comment) {
        return CommentUpdateResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(
                        Author.builder()
                                .authorId(comment.getAuthor().getId())
                                .authorName(comment.getAuthor().getUsername())
                                .authorProfileUrl(comment.getAuthor().getProfileImgUrl())
                                .build()
                )
                .build();
    }
}
