package com.circleon.domain.post.dto;

import com.circleon.domain.post.PostType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    private String content;

    @NotNull
    private PostType postType;

    private MultipartFile image;
}
