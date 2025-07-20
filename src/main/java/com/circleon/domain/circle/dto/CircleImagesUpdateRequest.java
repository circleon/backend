package com.circleon.domain.circle.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleImagesUpdateRequest {

    private MultipartFile profileImg;

    private MultipartFile introImg;
}
