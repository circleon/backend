package com.circleon.domain.post.service;

import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostDataService {

    void deletePosts(List<Post> posts);

    Page<Post> findAllByCircleIn(List<Circle> circles, Pageable pageable);
}
