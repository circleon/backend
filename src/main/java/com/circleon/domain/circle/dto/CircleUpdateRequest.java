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
public class CircleUpdateRequest {

    @NotBlank
    private String circleName;

    @NotNull
    private CategoryType categoryType;

    private MultipartFile profileImg;
}
