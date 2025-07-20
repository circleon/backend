package com.circleon.domain.post.repository;

import com.circleon.common.CommonStatus;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long>, PostImageRepositoryCustom {

    List<PostImage> findAllByPostIn(List<Post> posts);

    Optional<PostImage> findByPost(Post post);
}
