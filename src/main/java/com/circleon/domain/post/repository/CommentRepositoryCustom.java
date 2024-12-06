package com.circleon.domain.post.repository;

import com.circleon.domain.post.dto.CommentSearchResponse;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    List<CommentSearchResponse> findPagedCommentsByPostId(Long postId, Pageable pageable);

    Long countActiveCommentsByPostId(Long postId);

}
