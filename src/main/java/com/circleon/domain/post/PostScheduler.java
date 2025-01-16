package com.circleon.domain.post;

import com.circleon.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostScheduler {

    private final PostService postService;

    @Scheduled(cron = "0 0 3 ? * FRI")
    public void runSoftDeletedPostsCleanup() {
      try{
          log.info("Cleaning up SoftDeleted Posts");
          postService.deleteSoftDeletedPosts();
      } catch (RuntimeException e) {
          log.error("soft deleted posts failed", e);
      }
    }
}
