package com.circleon.domain.post.repository;

import com.circleon.domain.post.dto.CommentSearchResponse;

import com.circleon.domain.post.entity.Comment;
import com.circleon.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentSearchResponse> findPagedCommentsByPostId(Long postId, Pageable pageable);

    Long countActiveCommentsByPostId(Long postId);

    void deleteAllByPosts(List<Post> posts);

    void deleteAllByComments(List<Comment> comments);
}
