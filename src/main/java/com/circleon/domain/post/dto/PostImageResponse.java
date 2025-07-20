package com.circleon.domain.post.dto;

import com.circleon.domain.post.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostImageResponse {

    private String postImgUrl;

    public void changeToSignedUrl(String signedUrl) {
        postImgUrl = signedUrl;
    }

    public static PostImageResponse from(PostImage postImage) {
        return PostImageResponse.builder()
                .postImgUrl(postImage.getPostImgUrl())
                .build();
    }
}
