package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 35, message = "글자 수를 초과하였습니다.")
    private String summary;

    private LocalDateTime recruitmentStartDate;

    private LocalDateTime recruitmentEndDate;

    @NotNull
    private CategoryType categoryType;
    
}
