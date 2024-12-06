package com.circleon.domain.post.repository;

import com.circleon.common.CommonStatus;
import com.circleon.domain.post.entity.Comment;
import com.circleon.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findByIdAndPostAndStatus(Long Id, Post post, CommonStatus status);

    Optional<Comment> findByIdAndStatus(Long Id, CommonStatus status);
}
