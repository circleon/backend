package com.circleon.common.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {

    private List<T> content;

    private int currentPageNumber;

    private long totalElementCount;

    private int totalPageCount;

    public static <T> PaginatedResponse<T> of(List<T> content, int currentPageNumber, long totalElementCount, int totalPageCount) {
        return PaginatedResponse.<T>builder()
                .content(content)
                .currentPageNumber(currentPageNumber)
                .totalElementCount(totalElementCount)
                .totalPageCount(totalPageCount)
                .build();
    }
}
