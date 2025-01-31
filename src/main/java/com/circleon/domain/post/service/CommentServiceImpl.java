package com.circleon.domain.post.service;

import com.circleon.common.CommonResponseStatus;
import com.circleon.common.CommonStatus;
import com.circleon.common.dto.PaginatedResponse;
import com.circleon.common.exception.CommonException;
import com.circleon.domain.circle.CircleResponseStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;
import com.circleon.domain.circle.exception.CircleException;

import com.circleon.domain.circle.service.MyCircleDataService;
import com.circleon.domain.post.PostResponseStatus;
import com.circleon.domain.post.dto.*;
import com.circleon.domain.post.entity.Comment;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.exception.PostException;
import com.circleon.domain.post.repository.CommentRepository;
import com.circleon.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MyCircleDataService myCircleDataService;
    private final PostRepository postRepository;

    @Override
    public CommentCreateResponse createComment(RequestIdentifiers identifiers, CommentCreateRequest commentCreateRequest) {

        MyCircle member = validateMembership(identifiers.getUserId(), identifiers.getCircleId());

        Post post = validatePost(identifiers.getPostId(), member.getCircle(), "[createComment] 게시글이 존재하지 않음.");

        Comment comment = Comment.builder()
                .content(commentCreateRequest.getContent())
                .author(member.getUser())
                .post(post)
                .status(CommonStatus.ACTIVE)
                .build();

        Comment savedComment = commentRepository.save(comment);

        post.increaseCommentCount();

        return CommentCreateResponse.fromComment(savedComment);
    }

    private MyCircle validateMembership(Long userId, Long circleId) {
        return myCircleDataService.findJoinedMember(userId, circleId)
                .orElseThrow(() -> new CircleException(CircleResponseStatus.MEMBERSHIP_NOT_FOUND));
    }

    @Override
    public PaginatedResponse<CommentSearchResponse> findPagedComments(RequestIdentifiers identifiers, Pageable pageable) {

        MyCircle member = validateMembership(identifiers.getUserId(), identifiers.getCircleId());

        Post post = validatePost(identifiers.getPostId(), member.getCircle(), "[findPagedComments] 게시글이 존재하지 않음.");

        List<CommentSearchResponse> comments = commentRepository.findPagedCommentsByPostId(post.getId(), pageable);

        Long totalComments = commentRepository.countActiveCommentsByPostId(post.getId());

        Page<CommentSearchResponse> pagedComments = new PageImpl<>(comments, pageable, totalComments);

        post.setCommentCount(totalComments.intValue());

        return PaginatedResponse.fromPage(pagedComments);
    }

    private Post validatePost(Long postId, Circle circle, String exceptionMessage) {
        return postRepository.findByIdAndCircleAndStatus(postId, circle, CommonStatus.ACTIVE)
                .orElseThrow(() -> new PostException(PostResponseStatus.POST_NOT_FOUND, exceptionMessage));
    }

    @Override
    public CommentUpdateResponse updateComment(RequestIdentifiers identifiers, Long commentId, CommentUpdateRequest commentUpdateRequest) {

        validateMembership(identifiers.getUserId(), identifiers.getCircleId());

        Comment comment = commentRepository.findByIdAndStatus(commentId, CommonStatus.ACTIVE)
                .orElseThrow(() -> new PostException(PostResponseStatus.COMMENT_NOT_FOUND, "[updateComment] 댓글이 존재하지 않습니다."));

        validateCommentAuthor(comment, identifiers.getUserId(), "[updateComment] 본인이 작성하지 않은 댓글을 삭제하려는 시도");

        comment.setContent(commentUpdateRequest.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return CommentUpdateResponse.fromComment(comment);
    }

    @Override
    public void deleteComment(RequestIdentifiers identifiers, Long commentId) {

        MyCircle member = validateMembership(identifiers.getUserId(), identifiers.getCircleId());

        Post post = validatePost(identifiers.getPostId(), member.getCircle(), "[deleteComment] 게시글이 존재하지 않음.");

        Comment comment = commentRepository.findByIdAndPostAndStatus(commentId, post, CommonStatus.ACTIVE)
                .orElseThrow(() -> new PostException(PostResponseStatus.COMMENT_NOT_FOUND, "[deleteComment] 댓글이 존재하지 않습니다."));

        validateCommentAuthor(comment, identifiers.getUserId(), "[deleteComment] 본인이 작성하지 않은 댓글을 삭제하려는 시도");

        comment.setStatus(CommonStatus.INACTIVE);
        post.decreaseCommentCount();
    }

    private void validateCommentAuthor(Comment comment, Long userId, String message) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CommonException(CommonResponseStatus.FORBIDDEN_ACCESS, message);
        }
    }

    @Override
    public void deleteSoftDeletedComments() {

        Pageable pageable = PageRequest.of(0, 100);

        while (true){

            Page<Comment> pagedComments = commentRepository.findAllByStatus(CommonStatus.INACTIVE, pageable);
            List<Comment> comments = pagedComments.getContent();

            if(comments.isEmpty()){
                return;
            }

            //삭제
            commentRepository.deleteAllByComments(comments);
        }
    }
}
