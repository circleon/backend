package com.circleon.domain.post.service;

import com.circleon.domain.post.dto.PostCreateRequest;
import com.circleon.domain.post.dto.PostCreateResponse;

public interface PostService {

    PostCreateResponse createPost(Long userId, Long circleId, PostCreateRequest postCreateRequest);
}
