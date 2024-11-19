package com.circleon.domain.post.dto;

import com.circleon.domain.post.PostType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    private String content;

    private PostType postType;

    private MultipartFile image;
}
