package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleInfoUpdateRequest {

    @NotBlank
    private String circleName;

    private String introduction;

    private LocalDateTime recruitmentStartDate;

    private LocalDateTime recruitmentEndDate;

    @NotNull
    private CategoryType categoryType;
    
}
