package com.circleon.domain.post.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.CommonStatus;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleRole;

import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.CircleService;
import com.circleon.domain.circle.service.MyCircleService;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.*;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.repository.PostImageRepository;
import com.circleon.domain.post.repository.PostRepository;

import com.circleon.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final MyCircleService myCircleService;
    private final CircleService circleService;
    private final FileStore postFileStore;
    private final PostImageRepository postImageRepository;

    @Override
    public PostCreateResponse createPost(Long userId, Long circleId, PostCreateRequest postCreateRequest) {

        MyCircle member = validateMembership(userId, circleId);

        //동아리 원이 공지사항 작성 시에 예외
        validateAuthorizationForPostType(postCreateRequest.getPostType(), member.getCircleRole());

        //저장
        Post post = Post.builder()
                .postType(postCreateRequest.getPostType())
                .content(postCreateRequest.getContent())
                .author(member.getUser())
                .circle(member.getCircle())
                .status(CommonStatus.ACTIVE)
                .build();

        Post savedPost = postRepository.save(post);

        String postImgUrl = postFileStore.storeFile(postCreateRequest.getImage(), member.getCircle().getId());

        if(postImgUrl != null) {
            PostImage postImage = PostImage.builder()
                    .postImgUrl(postImgUrl)
                    .status(CommonStatus.ACTIVE)
                    .post(savedPost)
                    .build();
            postImageRepository.save(postImage);
        }

        return PostCreateResponse.fromPost(savedPost, postImgUrl);
    }

    private MyCircle validateMembership(Long userId, Long circleId) {
        return myCircleService.fineJoinedMember(userId, circleId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));
    }

    @Override
    public PaginatedResponse<PostResponse> findPagedPosts(Long userId, Long circleId, PostType postType, Pageable pageable) {

        // 멤버 검증
        MyCircle member = validateMembership(userId, circleId);

        List<PostResponse> posts = postRepository.findPosts(member.getCircle().getId(), postType, pageable);

        Long totalPosts = postRepository.countPosts(circleId, postType).getPostCount();

        Page<PostResponse> pagedPosts = new PageImpl<>(posts, pageable, totalPosts);

        return PaginatedResponse.fromPage(pagedPosts);
    }

    @Override
    public Resource loadImageAsResource(String filePath) {
        return postFileStore.loadFileAsResource(filePath);
    }

    @Override
    public PostUpdateResponse updatePost(Long userId, Long circleId, Long postId, PostUpdateRequest postUpdateRequest) {

        //회원
        MyCircle member = validateMembership(userId, circleId);

        //검증
        validateAuthorizationForPostType(postUpdateRequest.getPostType(), member.getCircleRole());

        //조회 및 저장
        Post post = postRepository.findByIdAndAuthorAndCircleAndStatus(postId, member.getUser(), member.getCircle(), CommonStatus.ACTIVE)
                .orElseThrow(()->new PostException(PostResponseStatus.POST_NOT_FOUND, "[updatePost] 게시글이 존재하지 않습니다."));

        //포스트
        post.setContent(postUpdateRequest.getContent());
        post.setPostType(postUpdateRequest.getPostType());

        return PostUpdateResponse.fromPost(post);
    }

    private void validateAuthorizationForPostType(PostType postType, CircleRole circleRole) {
        if (postType == PostType.NOTICE && circleRole == CircleRole.MEMBER) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS);
        }
    }

    @Override
    public void updatePin(Long userId, Long circleId, Long postId, PostPinUpdateRequest postPinUpdateRequest) {

        //회원
        MyCircle member = validateMembership(userId, circleId);

        //권한 검증
        if(member.getCircleRole() == CircleRole.MEMBER) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS);
        }

        Post post = postRepository.findByIdAndCircleAndStatus(postId, member.getCircle(), CommonStatus.ACTIVE)
                .orElseThrow(()->new PostException(PostResponseStatus.POST_NOT_FOUND, "[updatePost] 게시글이 존재하지 않습니다."));

        if(post.getPostType() == PostType.POST){
            throw new PostException(PostResponseStatus.NOT_NOTICE, "[updatePin] 공지사항이 아닙니다.");
        }

        //핀 수정
        post.setIsPinned(postPinUpdateRequest.getIsPinned());
    }

    //TODO 나중에 포스트 이미지랑 댓글은 스큐쥴러로 삭제
    @Override
    public void deletePost(Long userId, Long circleId, Long postId) {

        //회원
        MyCircle member = validateMembership(userId, circleId);

        Post post = postRepository.findByIdAndAuthorAndCircleAndStatus(postId, member.getUser(), member.getCircle(), CommonStatus.ACTIVE)
                .orElseThrow(() -> new PostException(PostResponseStatus.POST_NOT_FOUND, "[deletePost] 게시글이 존재하지 않습니다."));

        post.setStatus(CommonStatus.INACTIVE);
    }
}
