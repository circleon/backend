package com.circleon.domain.post.repository;

import com.circleon.common.CommonStatus;
import com.circleon.domain.post.entity.Post;
import com.circleon.domain.post.entity.PostImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.circleon.domain.post.entity.QPostImage.postImage;

@Repository
@RequiredArgsConstructor
public class PostImageRepositoryImpl implements PostImageRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public void deletePostImages(List<PostImage> postImages) {
        jpaQueryFactory
                .delete(postImage)
                .where(postImage.in(postImages))
                .execute();
    }
}
