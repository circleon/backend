package com.circleon.domain.post.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.*;
import com.circleon.domain.post.entity.Post;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    PostCreateResponse createPost(Long userId, Long circleId, PostCreateRequest postCreateRequest);

    PaginatedResponse<PostResponse> findPagedPosts(Long userId, Long circleId, PostType postType, Pageable pageable);

    Resource loadImageAsResource(String filePath, String expires, String signature);

    PostUpdateResponse updatePost(Long userId, Long circleId, Long postId, PostUpdateRequest postUpdateRequest);

    void updatePin(Long userId, Long circleId, Long postId, PostPinUpdateRequest postPinUpdateRequest);

    void softDeletePost(Long userId, Long circleId, Long postId);

    PostImageResponse findPost(Long userId, Long circleId, Long postId);

    void deleteSoftDeletedPosts();

}
