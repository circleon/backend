package com.circleon.domain.user.service;

import com.circleon.common.dto.PaginatedResponse;
import com.circleon.domain.post.repository.PostRepository;
import com.circleon.common.file.SignedUrlManager;

import com.circleon.domain.user.UserImageManager;
import com.circleon.domain.user.UserResponseStatus;
import com.circleon.domain.user.dto.CommentedPostResponse;
import com.circleon.domain.user.dto.MyPostResponse;

import com.circleon.domain.user.dto.UserInfo;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.circleon.domain.user.exception.UserException;
import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final SignedUrlManager signedUrlManager;
    private final UserRepository userRepository;
    private final UserImageManager userImageManager;

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

    @Transactional(readOnly = true)
    public UserInfo findMeById(Long loginId){
        User user = userRepository.findByIdAndStatus(loginId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        return UserInfo.from(user);
    }

    @Transactional
    public UserInfo updateMe(Long userId, String username){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));
        user.updateUserName(username);
        return UserInfo.from(user);
    }

    public void updateImage(Long userId, MultipartFile image){
        String oldPath = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND))
                .getProfileImgUrl();
        String url = userImageManager.saveImage(image, userId);
        saveImageMetaOrRollBack(userId, url);
        userImageManager.deleteImage(oldPath);
    }

    private void saveImageMetaOrRollBack(Long userId, String url) {
        try{
            userImageManager.updateImageMeta(userId, url);
        } catch (RuntimeException e) {
            userImageManager.deleteImage(url);
        }
    }

    public void deleteImage(Long userId){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserResponseStatus.USER_NOT_FOUND));

        userImageManager.updateImageMeta(userId, null);
        userImageManager.deleteImage(user.getProfileImgUrl());
    }
}
