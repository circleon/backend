package com.circleon.config.dataloader;

import com.circleon.common.CommonStatus;
import com.circleon.domain.circle.CircleRole;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.repository.CircleRepository;
import com.circleon.domain.circle.repository.MyCircleRepository;
import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.PostResponse;
import com.circleon.domain.post.entity.Comment;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.repository.CommentRepository;
import com.circleon.domain.post.repository.PostRepository;


import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Order(3)
public class PostDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CircleRepository circleRepository;
    private final PostRepository postRepository;
    private final MyCircleRepository myCircleRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) throws Exception {

        if(postRepository.count() != 0){
            return;
        }

        List<MyCircle> members = myCircleRepository.findAllByIdLessThanEqual(5L);

        createPostTestData(members);
        createNoticeTestData(members);
        createCommentData(members);
    }

    private void createCommentData(List<MyCircle> members) {
        List<Comment> comments = new ArrayList<>();

        for (MyCircle mc : members) {

            if(mc.getMembershipStatus() != MembershipStatus.APPROVED) continue;

            Pageable pageable = PageRequest.of(0, 20);

            List<PostResponse> posts = postRepository.findPosts(mc.getCircle().getId(), PostType.POST, pageable);

            for (PostResponse post : posts) {

                Optional<Post> savedPost = postRepository.findById(post.getPostId());
                if(savedPost.isEmpty()) continue;

                Post postEntity = savedPost.get();
                for(int i = 0 ; i < 20; i++){

                    String content = i + "번 댓글입니다. 안녕하세요.";

                    Comment comment = Comment.builder()
                            .content(content)
                            .status(CommonStatus.ACTIVE)
                            .author(mc.getUser())
                            .post(postEntity)
                            .build();
                    postEntity.increaseCommentCount();
                    postRepository.save(postEntity);

                    comments.add(comment);

                }
            }

        }

        commentRepository.saveAll(comments);
    }

    private void createPostTestData(List<MyCircle> members) {
        List<Post> posts = new ArrayList<>();

        for (MyCircle mc : members) {

            if(mc.getMembershipStatus() != MembershipStatus.APPROVED) continue;

            for(int i = 0 ; i < 500; i++){

                String content = i + "번 게시글입니다. 안뇽하세염";


                Post post = Post.builder()
                        .content(content)
                        .postType(PostType.POST)
                        .status(CommonStatus.ACTIVE)
                        .author(mc.getUser())
                        .circle(mc.getCircle())
                        .isPinned(false)
                        .build();

                posts.add(post);
            }
        }

        postRepository.saveAll(posts);
    }

    private void createNoticeTestData(List<MyCircle> members) {
        List<Post> posts = new ArrayList<>();

        for (MyCircle mc : members) {

            if(mc.getCircleRole() == CircleRole.MEMBER) continue;

            for(int i = 0 ; i < 10; i++){

                Boolean isPinned = false;

                if(i % 4 == 0){
                    isPinned = true;
                }

                String content = i + "번 공지사항입니다. 안뇽하세염";

                Post post = Post.builder()
                        .content(content)
                        .postType(PostType.NOTICE)
                        .status(CommonStatus.ACTIVE)
                        .author(mc.getUser())
                        .circle(mc.getCircle())
                        .isPinned(isPinned)
                        .build();
                posts.add(post);
            }
        }

        postRepository.saveAll(posts);

    }
}
