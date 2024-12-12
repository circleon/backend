package com.circleon.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CommentCreateRequest {

    @NotBlank
    private String content;

}
