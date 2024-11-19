package com.circleon.domain.post.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.CommonStatus;
import com.circleon.common.exception.CommonException;
import com.circleon.common.file.FileStore;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;
import com.circleon.domain.circle.service.CircleService;
import com.circleon.domain.circle.service.MyCircleService;
import com.circleon.domain.post.dto.PostCreateRequest;
import com.circleon.domain.post.dto.PostCreateResponse;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;
import com.circleon.domain.post.repository.PostImageRepository;
import com.circleon.domain.post.repository.PostRepository;
import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


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

        //존재하는 써클인지 확인
        Circle circle = circleService.findByIdAndCircleStatus(circleId, CircleStatus.ACTIVE)
                .orElseThrow(()->new CircleException(CircleResponseStatus.CIRCLE_NOT_FOUND));

        User user = userService.findById(userId)
                .orElseThrow(() -> new CommonException(CommonResponseStatus.USER_NOT_FOUND));

        //해당 써클의 멤버인지 확인
        myCircleService.findByUserAndCircleAndMembershipStatus(user, circle, MembershipStatus.APPROVED)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));

        //저장
        Post post = Post.builder()
                .postType(postCreateRequest.getPostType())
                .content(postCreateRequest.getContent())
                .author(user)
                .circle(circle)
                .status(CommonStatus.ACTIVE)
                .build();

        Post savedPost = postRepository.save(post);

        String postImgUrl = postFileStore.storeFile(postCreateRequest.getImage(), circle.getId());

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
}
