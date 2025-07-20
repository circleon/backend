package com.circleon.domain.post.service;

import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommentDataServiceImpl implements CommentDataService {

    private final CommentRepository commentRepository;

    @Override
    public void deleteAllByPosts(List<Post> posts) {
        commentRepository.deleteAllByPosts(posts);
    }
}
