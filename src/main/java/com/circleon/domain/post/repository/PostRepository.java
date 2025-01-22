package com.circleon.domain.post.repository;

import com.circleon.common.CommonStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findByIdAndAuthorAndCircleAndStatus(Long id, User author, Circle circle, CommonStatus status);

    Optional<Post> findByIdAndCircleAndStatus(Long id, Circle circle, CommonStatus status);

    Page<Post> findAllByStatus(CommonStatus status, Pageable pageable);

    Page<Post> findAllByCircleIn(List<Circle> circles, Pageable pageable);

}
