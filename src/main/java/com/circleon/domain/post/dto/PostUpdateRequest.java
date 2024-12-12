package com.circleon.domain.post.dto;

import com.circleon.domain.post.PostType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

    private String content;

    @NotNull
    private PostType postType;
}
