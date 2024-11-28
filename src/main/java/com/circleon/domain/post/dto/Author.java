package com.circleon.domain.post.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    private Long authorId;

    private String authorName;

    private String authorProfileUrl;
}
