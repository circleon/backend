package com.circleon.domain.user.dto;

import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentedPostResponse {

    private Long postId;

    private String content;

    private PostType postType;

    private Integer commentCount;

    private Boolean isPinned;

    private String postImgUrl;

    private Long circleId;

    private String circleName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Author postAuthor;



    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }
}
