package com.circleon.common;

import com.circleon.common.exception.CommonException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageableValidator {

    private PageableValidator(){}

    public static void validatePageable(Pageable pageable, List<String> allowedSortFields, int maxPageSize) {

        pageable.getSort().forEach(order -> {
            if(!allowedSortFields.contains(order.getProperty())) {
                throw new CommonException(CommonResponseStatus.BAD_REQUEST, "허용되지 않은 필드로 정렬 시도");
            }
        });

        if(pageable.getPageSize() > maxPageSize) {
            throw new CommonException(CommonResponseStatus.BAD_REQUEST, "페이지 최대 사이즈를 초과");
        }
    }
}
