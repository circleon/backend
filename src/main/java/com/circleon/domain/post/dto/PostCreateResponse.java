package com.circleon.domain.post.dto;

import com.circleon.domain.post.PostType;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateResponse {

    private Long postId;

    private Long authorId;

    private String authorName;

    private String content;

    private LocalDateTime createdAt;

    private PostType postType;

    private String imageUrl;

    public static PostCreateResponse fromPost(Post post, String imageUrl) {
        return PostCreateResponse.builder()
                .postId(post.getId())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getUsername())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .postType(post.getPostType())
                .imageUrl(imageUrl)
                .build();

    }
}
