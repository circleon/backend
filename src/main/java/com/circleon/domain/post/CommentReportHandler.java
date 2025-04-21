package com.circleon.domain.post;

import com.circleon.common.exception.CommonException;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.repository.CommentRepository;
import com.circleon.domain.report.ReportHandler;
import com.circleon.domain.report.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentReportHandler implements ReportHandler {

    private final CommentRepository commentRepository;

    @Override
    public ReportType getType() {
        return ReportType.COMMENT;
    }

    @Override
    public void validateTargetExist(Long targetId) {
        if(!commentRepository.existsById(targetId)) {
            throw new PostException(PostResponseStatus.COMMENT_NOT_FOUND, "[CommentReportHandler] 댓글이 존재하지 않습니다.");
        }
    }
}
