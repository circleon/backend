package com.circleon.common;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
public class SortUtils {

    private SortUtils() {}

    public static <T> OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, Class<T> clazz, EntityPath<T> entityPath) {
        return sort.stream()
                .map(order -> {

                    Class<?> fieldType = getFieldType(clazz, order.getProperty());
                    PathBuilder<T> pathBuilder = new PathBuilder<>(entityPath.getType(), entityPath.getMetadata());

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

                    if(fieldType == Boolean.class) {
                        return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.getBoolean(order.getProperty()));
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }

    private static <T>Class<?> getFieldType(Class<T> clazz, String fieldName) {

        Class<? super T> currentClass = clazz;

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
}
