package com.circleon.domain.post.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPinUpdateRequest {

    @NotNull
    private Boolean isPinned;
}
