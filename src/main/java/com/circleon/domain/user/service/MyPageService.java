package com.circleon.domain.user.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.post.repository.PostRepository;
import com.circleon.domain.post.service.SignedUrlManager;

import com.circleon.domain.user.dto.CommentedPostResponse;
import com.circleon.domain.user.dto.MyPostResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final SignedUrlManager signedUrlManager;

    @Transactional(readOnly = true)
    public PaginatedResponse<MyPostResponse> findMyPosts(Long userId, Pageable pageable) {

        //게시글 조회
        PaginatedResponse<MyPostResponse> myPosts = postRepository.findMyPosts(userId, pageable);

        //signedUrl 생성
        myPosts.getContent().forEach(post -> {
            String originUrl = post.getPostImgUrl();

            if(originUrl != null && !originUrl.isBlank()) {
                post.setPostImgUrl(signedUrlManager.createSignedUrl(originUrl));
            }
        });

        return myPosts;
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<CommentedPostResponse> findMyCommentedPosts(Long userId, Pageable pageable) {

        //게시글 조회
        PaginatedResponse<CommentedPostResponse> myCommentedPosts = postRepository.findMyCommentedPosts(userId, pageable);

        myCommentedPosts.getContent().forEach(post -> {
            String originUrl = post.getPostImgUrl();

            if(originUrl != null && !originUrl.isBlank()) {
                post.setPostImgUrl(signedUrlManager.createSignedUrl(originUrl));
            }
        });

        return myCommentedPosts;
    }
}
