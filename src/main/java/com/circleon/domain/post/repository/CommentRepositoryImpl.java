package com.circleon.domain.post.repository;

import com.circleon.common.CommonStatus;
import com.circleon.common.SortUtils;
import com.circleon.domain.post.dto.Author;
import com.circleon.domain.post.dto.CommentSearchResponse;

import com.circleon.domain.post.entity.Comment;
import com.circleon.domain.post.entity.Post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Repository;


import java.util.List;


import static com.circleon.domain.post.entity.QComment.comment;

import static com.circleon.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommentSearchResponse> findPagedCommentsByPostId(Long postId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(CommentSearchResponse.class,
                        comment.id,
                        comment.content,
                        comment.createdAt,
                        comment.updatedAt,
                        Projections.constructor(Author.class,
                                comment.author.id,
                                comment.author.username,
                                comment.author.profileImgUrl
                        ))
                )
                .from(comment)
                .join(comment.author, user)
                .where(
                        postIdEq(postId),
                        isCommentActive()
                )
                .orderBy(SortUtils.getOrderSpecifiers(pageable.getSort(), Comment.class, comment))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression postIdEq(Long postId) {
        return postId != null ? comment.post.id.eq(postId) : null;
    }

    private BooleanExpression isCommentActive(){
        return comment.status.eq(CommonStatus.ACTIVE);
    }

    @Override
    public Long countActiveCommentsByPostId(Long postId) {
        return jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        postIdEq(postId),
                        isCommentActive()
                ).fetchOne();
    }

    @Override
    public void deleteAllByPosts(List<Post> posts) {
        jpaQueryFactory
                .delete(comment)
                .where(comment.post.in(posts))
                .execute();
    }
}
