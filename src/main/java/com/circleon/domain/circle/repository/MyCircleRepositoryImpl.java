package com.circleon.domain.circle.repository;

import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.MembershipStatus;
import com.circleon.domain.circle.dto.CircleInfo;

import com.circleon.domain.circle.dto.MyCircleSearchRequest;
import com.circleon.domain.circle.dto.MyCircleSearchResponse;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.circle.entity.MyCircle;

import com.circleon.domain.user.entity.User;
import com.circleon.domain.user.entity.UserStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.circleon.domain.circle.entity.QCircle.*;
import static com.circleon.domain.circle.entity.QMyCircle.*;
import static com.circleon.domain.user.entity.QUser.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MyCircleRepositoryImpl implements MyCircleRepositoryCustom{

    private static final String USERNAME = "username";
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<MyCircle> findAllByCircleAndMembershipStatusWithUser(Circle circle, MembershipStatus membershipStatus, Pageable pageable) {

        List<MyCircle> content = jpaQueryFactory
                .selectFrom(myCircle)
                .join(myCircle.user, user).fetchJoin()
                .where(myCircle.circle.eq(circle)
                        .and(myCircle.membershipStatus.eq(membershipStatus)))
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                    jpaQueryFactory
                    .select(myCircle.count())
                    .from(myCircle)
                    .where(myCircle.circle.eq(circle)
                            .and(myCircle.membershipStatus.eq(membershipStatus)))
                    .fetchOne()
                ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        return sort.stream()
                .map(order -> {

                    if(USERNAME.equals(order.getProperty())) {
                        return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, user.username);
                    }

                    Class<?> fieldType = getFieldType(order.getProperty());
                    PathBuilder<MyCircle> pathBuilder = new PathBuilder<>(myCircle.getType(), myCircle.getMetadata());

                    if(fieldType == LocalDateTime.class){
                        return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.getDateTime(order.getProperty(), LocalDateTime.class));
                    }

                    if(fieldType == String.class) {
                        return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.getString(order.getProperty()));
                    }

                    if(fieldType == Long.class) {
                        return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.getNumber(order.getProperty(), Long.class));
                    }

                    if(fieldType == Integer.class) {
                        return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.getNumber(order.getProperty(), Integer.class));
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }

    private Class<?> getFieldType(String fieldName) {

        Class<?> currentClass = MyCircle.class;

        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                return field.getType();
            }catch (NoSuchFieldException e){
                currentClass = currentClass.getSuperclass();
            }
        }

        log.warn("정렬을 위한 필드 체크 에러. 존재하는 필드가 없습니다. {}", fieldName);
        return null;
    }

    @Override
    public Optional<MyCircle> findAllByUserAndCircleInMembershipStatuses(User user, Circle circle, List<MembershipStatus> membershipStatuses) {

        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(myCircle)
                .where(myCircle.circle.eq(circle)
                        .and(myCircle.user.eq(user))
                        .and(myCircle.membershipStatus.in(membershipStatuses)))
                .fetchFirst());
    }

    //TODO 삭제
//    public Optional<MyCircle> findByMyCircleSearchCondition(MyCircleSearchCondition condition) {
//
//        return Optional.ofNullable(
//                jpaQueryFactory
//                        .selectFrom(myCircle)
//                        .join(myCircle.circle, circle).fetchJoin()
//                        .join(myCircle.user, user).fetchJoin()
//                        .where(userIdEq(condition.getUserId()),
//                                userStatusEq(condition.getUserStatus()),
//                                circleIdEq(condition.getCircleId()),
//                                circleStatusEq(condition.getCircleStatus()),
//                                membershipStatusEq(condition.getMembershipStatus())
//                                        .or(membershipStatusEq(MembershipStatus.LEAVE_REQUEST))
//                        ).fetchOne()
//        );
//    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

    private BooleanExpression userStatusEq(UserStatus userStatus) {
        return userStatus != null ? user.status.eq(userStatus) : user.status.eq(UserStatus.ACTIVE);
    }

    private BooleanExpression circleIdEq(Long circleId){
        return circleId != null ? circle.id.eq(circleId) : null;
    }

    private BooleanExpression circleStatusEq(CircleStatus circleStatus) {
        return circleStatus != null ? circle.circleStatus.eq(circleStatus) : circle.circleStatus.eq(CircleStatus.ACTIVE);
    }

    private BooleanExpression membershipStatusEq(MembershipStatus membershipStatus) {
        return membershipStatus != null ? myCircle.membershipStatus.eq(membershipStatus) : myCircle.membershipStatus.eq(MembershipStatus.APPROVED);
    }

    @Override
    public Optional<MyCircle> fineJoinedMember(Long userId, Long circleId) {

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(myCircle)
                        .join(myCircle.circle, circle).fetchJoin()
                        .join(myCircle.user, user).fetchJoin()
                        .where(userIdEq(userId),
                                userStatusEq(UserStatus.ACTIVE),
                                circleIdEq(circleId),
                                circleStatusEq(CircleStatus.ACTIVE),
                                membershipStatusEq(MembershipStatus.APPROVED)
                                        .or(membershipStatusEq(MembershipStatus.LEAVE_REQUEST))
                        ).fetchOne()
        );
    }

    @Override
    public Page<MyCircleSearchResponse> findAllByMyCircleSearchRequest(MyCircleSearchRequest myCircleSearchRequest) {

        List<MyCircleSearchResponse> myCircleSearchResponses = jpaQueryFactory
                .select(Projections.constructor(MyCircleSearchResponse.class,
                        myCircle.id,
                        myCircle.joinedAt,
                        myCircle.membershipStatus,
                        myCircle.circleRole,
                        Projections.constructor(CircleInfo.class,
                                myCircle.circle.id,
                                myCircle.circle.name,
                                myCircle.circle.thumbnailUrl
                        )))
                .from(myCircle)
                .join(myCircle.circle, circle)
                .where(
                        userIdEq(myCircleSearchRequest.getUserId()),
                        membershipStatusEq(myCircleSearchRequest.getMembershipStatus()),
                        circleStatusEq(CircleStatus.ACTIVE)
                )
                .orderBy(getOrderSpecifiers(myCircleSearchRequest.getPageable().getSort()))
                .offset(myCircleSearchRequest.getPageable().getOffset())
                .limit(myCircleSearchRequest.getPageable().getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                jpaQueryFactory
                .select(myCircle.count())
                .from(myCircle)
                .where(
                        userIdEq(myCircleSearchRequest.getUserId()),
                        membershipStatusEq(myCircleSearchRequest.getMembershipStatus()),
                        circleStatusEq(CircleStatus.ACTIVE)
                ).fetchOne())
                .orElse(0L);

        return new PageImpl<>(myCircleSearchResponses, myCircleSearchRequest.getPageable(), total);
    }

    @Override
    public Optional<MyCircle> findByIdWithUserAndCircle(Long myCircleId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(myCircle)
                        .from(myCircle)
                        .join(myCircle.user, user).fetchJoin()
                        .join(myCircle.circle, circle).fetchJoin()
                        .where(
                                myCircle.id.eq(myCircleId)
                        ).fetchOne()
        );
    }
}
