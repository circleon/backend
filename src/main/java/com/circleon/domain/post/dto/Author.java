package com.circleon.domain.post.dto;

import com.circleon.domain.user.dto.UserInfo;
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
