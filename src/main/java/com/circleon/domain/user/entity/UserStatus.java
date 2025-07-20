package com.circleon.domain.user.entity;

import com.circleon.common.converter.AbstractCommonEnumAttributeConverter;
import com.circleon.common.converter.CommonEnum;

import lombok.Getter;

@Getter
public enum UserStatus implements CommonEnum {
    ACTIVE("활성화"),
    DEACTIVATED("비활성화"),
    PENDING("대기");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    @jakarta.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractCommonEnumAttributeConverter<UserStatus> {
        public Converter() {
            super(UserStatus.class);
        }
    }
}
