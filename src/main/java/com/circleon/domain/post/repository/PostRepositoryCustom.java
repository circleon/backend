package com.circleon.domain.post.repository;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.PostCount;
import com.circleon.domain.post.dto.PostResponse;

import com.circleon.domain.post.entity.Post;
import com.circleon.domain.user.dto.CommentedPostResponse;
import com.circleon.domain.user.dto.MyPostResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    List<PostResponse> findPosts(Long circleId, PostType postType, Pageable pageable);

    PaginatedResponse<MyPostResponse> findMyPosts(Long userId, Pageable pageable);

    PaginatedResponse<CommentedPostResponse> findMyCommentedPosts(Long userId, Pageable pageable);

    PostCount countPosts(Long circleId, PostType postType);

    void deletePostsBy(List<Post> posts);

}
