package com.circleon.domain.post.repository;

import com.circleon.common.CommonStatus;

import com.circleon.common.SortUtils;
import com.circleon.domain.post.PostType;
import com.circleon.domain.post.dto.Author;
import com.circleon.domain.post.dto.PostCount;
import com.circleon.domain.post.dto.PostResponse;

import com.circleon.domain.post.entity.Comment;
import com.circleon.domain.post.entity.Post;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.circleon.domain.circle.entity.QCircle.*;
import static com.circleon.domain.user.entity.QUser.*;
import static com.circleon.domain.post.entity.QPost.*;
import static com.circleon.domain.post.entity.QPostImage.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PostResponse> findPosts(Long circleId, PostType postType, Pageable pageable) {

        return jpaQueryFactory.select(Projections.constructor(PostResponse.class,
                        post.id,
                        post.isPinned,
                        postImage.postImgUrl,
                        post.content,
                        post.postType,
                        post.createdAt,
                        post.updatedAt,
                        post.commentCount,
                        Projections.constructor(Author.class,
                                post.author.id,
                                post.author.username,
                                post.author.profileImgUrl
                        )))
                .from(post)
                .join(post.author, user)
                .leftJoin(postImage).on(postImage.post.id.eq(post.id),postImage.status.eq(CommonStatus.ACTIVE))
                .where(
                        circleIdEq(circleId),
                        postTypeEq(postType),
                        post.status.eq(CommonStatus.ACTIVE)
                        )
                .orderBy(SortUtils.getOrderSpecifiers(pageable.getSort(), Post.class, post))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    // TODO 캐시를 꼭 해야 하는지 테스트 해봐야함
//    @Cacheable(value = "postCount", key = "#circleId + ':' + #postType")
    @Override
    public PostCount countPosts(Long circleId, PostType postType) {

        Long postCount = Optional.ofNullable(
                jpaQueryFactory
                        .select(post.count())
                        .from(post)
                        .where(
                                circleIdEq(circleId),
                                postTypeEq(postType),
                                post.status.eq(CommonStatus.ACTIVE)
                        )
                        .fetchOne()
        ).orElse(0L);

        return PostCount.builder().postCount(postCount).build();
    }

    private BooleanExpression circleIdEq(Long circleId){
        return circleId != null ? circle.id.eq(circleId) : null;
    }

    private BooleanExpression postTypeEq(PostType postType){
        return postType != null ? post.postType.eq(postType) : null;
    }

    @Override
    public void deletePostsBy(List<Post> posts) {
        jpaQueryFactory
                .delete(post)
                .where(post.in(posts))
                .execute();
    }
}
