package com.circleon.domain.admin.dto;

import com.circleon.domain.circle.OfficialStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class CircleUpdateOfficialRequest {

    @NotNull
    private OfficialStatus officialStatus;
}
