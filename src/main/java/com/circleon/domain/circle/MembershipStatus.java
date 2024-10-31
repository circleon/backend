package com.circleon.domain.circle;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;

import lombok.Getter;

@Getter
public enum MembershipStatus implements CommonEnum {
    PENDING("대기"),
    APPROVED("가입"),
    REJECTED("거절"),
    INACTIVE("탈퇴");

    private final String description;

    MembershipStatus(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<MembershipStatus> {
        public Converter() {
            super(MembershipStatus.class);
        }
    }
}
