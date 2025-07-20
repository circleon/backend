package com.circleon.domain.circle.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleListResponse<T> {

    private List<T> content;

    public static <T>CircleListResponse<T>fromList(List<T> content) {
        return CircleListResponse.<T>builder().content(content).build();
    }
}
