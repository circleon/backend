package com.circleon.domain.post.repository;

import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.PostCount;
import com.circleon.domain.post.dto.PostResponse;

import com.circleon.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {

    List<PostResponse> findPosts(Long circleId, PostType postType, Pageable pageable);

    PostCount countPosts(Long circleId, PostType postType);

    void deletePostsBy(List<Post> posts);

}
