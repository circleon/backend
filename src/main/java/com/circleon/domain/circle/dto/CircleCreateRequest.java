package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleCreateRequest {

    @NotBlank
    private String circleName;

    @NotNull
    private CategoryType category;

    @NotBlank
    private String introduction;

    private MultipartFile profileImg;
}
