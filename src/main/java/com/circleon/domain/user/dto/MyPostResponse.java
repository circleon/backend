package com.circleon.domain.user.dto;

import com.circleon.domain.post.PostType;

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

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }
}
