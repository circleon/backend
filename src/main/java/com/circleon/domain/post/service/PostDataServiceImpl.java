package com.circleon.domain.post.service;

import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostDataServiceImpl implements PostDataService {

    private final PostRepository postRepository;

    @Override
    public void deletePosts(List<Post> posts){
        postRepository.deletePostsBy(posts);
    }

    @Override
    public Page<Post> findAllByCircleIn(List<Circle> circles, Pageable pageable) {
        return postRepository.findAllByCircleIn(circles, pageable);
    }
}
