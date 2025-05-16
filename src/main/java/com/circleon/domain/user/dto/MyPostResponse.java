package com.circleon.domain.user.dto;

import com.circleon.domain.post.PostType;

import com.circleon.domain.post.dto.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MyPostResponse {

    private Long postId;

    private String content;

    private PostType postType;

    private Integer commentCount;

    private Boolean isPinned;

    private String postImgUrl;

    private Long circleId;

    private String circleName;

    private Author postAuthor;

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }
}
