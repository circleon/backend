package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.entity.Circle;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CircleImagesUpdateResponse {

    private String profileImgUrl;

    private String thumbnailUrl;

    private String introImgUrl;

    public static CircleImagesUpdateResponse fromCircle(Circle circle) {
        return CircleImagesUpdateResponse.builder()
                .profileImgUrl(circle.getProfileImgUrl())
                .thumbnailUrl(circle.getThumbnailUrl())
                .introImgUrl(circle.getIntroImgUrl())
                .build();
    }

}
