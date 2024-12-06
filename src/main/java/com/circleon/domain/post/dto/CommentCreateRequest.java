package com.circleon.domain.post.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CommentCreateRequest {

    private String content;

}
