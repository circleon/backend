package com.circleon.domain.post.service;

import com.circleon.domain.post.entity.Post;

import java.util.List;

public interface CommentDataService {

    void deleteAllByPosts(List<Post> posts);
}
