package com.circleon.domain.post.service;

import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;
import com.circleon.domain.post.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostImageDataServiceImpl implements PostImageDataService {

    private final PostImageRepository postImageRepository;

    @Override
    public List<PostImage> findAllByPostIn(List<Post> posts) {
        return postImageRepository.findAllByPostIn(posts);
    }

    @Override
    public void deletePostImages(List<PostImage> postImages) {
        postImageRepository.deletePostImages(postImages);
    }
}
