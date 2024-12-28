package com.circleon.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateRequest {

    @NotBlank
    @Size(max = 255, message = "댓글은 최대 255자까지 입력할 수 있습니다.")
    private String content;

}
