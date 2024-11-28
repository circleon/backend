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
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.repository.PostRepository;


import com.circleon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(3)
public class PostDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CircleRepository circleRepository;
    private final PostRepository postRepository;
    private final MyCircleRepository myCircleRepository;

    @Override
    public void run(String... args) throws Exception {

        if(postRepository.count() != 0){
            return;
        }

        List<MyCircle> members = myCircleRepository.findAllByIdLessThanEqual(300L);

        createPostTestData(members);
        createNoticeTestData(members);
    }

    private void createPostTestData(List<MyCircle> members) {
        List<Post> posts = new ArrayList<>();

        for (MyCircle mc : members) {

            if(mc.getMembershipStatus() != MembershipStatus.APPROVED) continue;

            for(int i = 0 ; i < 3; i++){

                String content = i + "번 게시글입니다. 안뇽하세염";

                Post post = Post.builder()
                        .content(content)
                        .postType(PostType.POST)
                        .status(CommonStatus.ACTIVE)
                        .author(mc.getUser())
                        .circle(mc.getCircle())
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

            for(int i = 0 ; i < 3; i++){

                String content = i + "번 공지사항입니다. 안뇽하세염";

                Post post = Post.builder()
                        .content(content)
                        .postType(PostType.NOTICE)
                        .status(CommonStatus.ACTIVE)
                        .author(mc.getUser())
                        .circle(mc.getCircle())
                        .build();
                posts.add(post);
            }
        }

        postRepository.saveAll(posts);

    }
}
