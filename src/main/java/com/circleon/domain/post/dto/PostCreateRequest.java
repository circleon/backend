package com.circleon.domain.post.dto;

import com.circleon.domain.post.PostType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    @Size(max = 1000, message = "게시글은 최대 1000자까지 입력할 수 있습니다.")
    private String content;

    @NotNull
    private PostType postType;

    private MultipartFile image;
}
