package com.circleon.domain.report.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class ReportCreateRequest {

    @NotBlank
    private String reason;
}
