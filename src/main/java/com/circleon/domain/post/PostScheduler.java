package com.circleon.domain.post;

import com.circleon.domain.post.service.CommentService;
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
    private final CommentService commentService;

    @Scheduled(cron = "0 0 3 ? * FRI")
    public void cleanUpSoftDeletedPosts() {
      try{
          log.info("Cleaning up SoftDeleted Posts");
          postService.deleteSoftDeletedPosts();
      } catch (Exception e) {
          log.error("Cleaning up SoftDeleted Posts fail", e);
      }
    }

    @Scheduled(cron = "0 0 3 ? * MON")
    public void cleanUpSoftDeletedComments() {
        try{
            log.info("Cleaning up SoftDeleted Comments");
            commentService.deleteSoftDeletedComments();
        } catch (Exception e) {
            log.error("Cleaning up SoftDeleted Comments fail", e);
        }
    }
}
