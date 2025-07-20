package com.circleon.domain.circle.dto;

import com.circleon.domain.circle.CategoryType;
import com.circleon.domain.circle.CircleStatus;
import com.circleon.domain.circle.OfficialStatus;
import com.circleon.domain.circle.entity.Circle;
import com.circleon.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleCreateRequest {

    @NotBlank
    @Size(max = 255, message = "동아리 이름은 최대 255자까지 입력할 수 있습니다.")
    private String circleName;

    @NotNull
    private CategoryType category;

    @NotBlank
    @Size(max = 35, message = "글자 수를 초과하였습니다.")
    private String summary;

    private MultipartFile profileImg;

    private LocalDateTime recruitmentStartDate;

    private LocalDateTime recruitmentEndDate;

    private boolean recruiting;

    @Size(max = 1000, message = "소개글은 최대 1000자까지 입력할 수 있습니다.")
    private String introduction;

    private MultipartFile introductionImg;

    public Circle toCircle(User user) {
        return Circle.builder()
                .applicant(user)
                .name(circleName)
                .circleStatus(CircleStatus.ACTIVE)
                .categoryType(category)
                .introduction(introduction)
                .summary(summary)
                .recruitmentStartDate(recruitmentStartDate)
                .recruitmentEndDate(recruitmentEndDate)
                .officialStatus(OfficialStatus.UNOFFICIAL)
                .memberCount(1)
                .isRecruiting(recruiting)
                .build();
    }
}
