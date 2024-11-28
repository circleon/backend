package com.circleon.domain.post.dto;


import com.circleon.domain.post.PostType;
import com.circleon.domain.post.entity.Post;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateResponse {


    private String content;

    private PostType postType;

    public static PostUpdateResponse fromPost(Post post) {
        return PostUpdateResponse.builder()
                .content(post.getContent())
                .postType(post.getPostType())
                .build();
    }
}
