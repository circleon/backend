package com.circleon.domain.post;

import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.repository.PostRepository;
import com.circleon.domain.report.ReportHandler;
import com.circleon.domain.report.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostReportHandler implements ReportHandler {

    private final PostRepository postRepository;

    @Override
    public ReportType getType() {
        return ReportType.POST;
    }

    @Override
    public void validateTargetExist(Long targetId) {
        if(!postRepository.existsById(targetId)) {
            throw new PostException(PostResponseStatus.POST_NOT_FOUND, "[PostReportHandler] 게시글이 존재하지 않습니다.");
        }
    }
}
