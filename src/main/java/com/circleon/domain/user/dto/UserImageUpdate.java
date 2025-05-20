package com.circleon.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@Setter
public class UserImageUpdate {

    @NotNull(message = "이미지는 필수입니다.")
    private MultipartFile image;
}
