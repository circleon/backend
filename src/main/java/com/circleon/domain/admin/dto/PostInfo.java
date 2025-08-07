package com.circleon.domain.admin.dto;

import com.circleon.domain.post.entity.Post;

public record PostInfo(
        Long postId,
        String content,
        Long authorId,
        String authorName
) {
    public static PostInfo from(Post post) {
        return new PostInfo(
                post.getId(),
                post.getContent(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername());
    }

    public static PostInfo empty() {
        return new PostInfo(
                null,
                "",
                null,
                "알 수 없음"
        );
    }
}
