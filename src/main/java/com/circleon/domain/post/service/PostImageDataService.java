package com.circleon.domain.post.service;

import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;

import java.util.List;

public interface PostImageDataService {

    List<PostImage> findAllByPostIn(List<Post> posts);

    void deletePostImages(List<PostImage> postImages);
}
